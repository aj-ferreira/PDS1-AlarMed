package com.example.alarmed.ui.medicamentos.list;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmed.R;
import com.example.alarmed.alarm.NotificationHelper;
import com.example.alarmed.alarm.StockManager;
import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.ui.historico.HistoricoActivity;
import com.example.alarmed.ui.horario.HorarioActivity;
import com.example.alarmed.ui.medicamentos.addedit.AddEditMedicamentoActivity;
import com.example.alarmed.ui.medicamentos.detail.MedicamentoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private MedicamentoViewModel mMedicamentoViewModel;

    private ActivityResultLauncher<Intent> mNewMedicamentoActivityLauncher;

    // Launcher para pedir a permissão de notificação
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                Log.d("MainActivity", "Resultado da permissão de notificação: " + isGranted);
                if (isGranted) {
                    Toast.makeText(this, "Permissão de notificação concedida.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permissão negada. Lembretes podem não funcionar.", Toast.LENGTH_LONG).show();
                }
            });

    // Launcher para a tela de Adicionar/Editar
    private final ActivityResultLauncher<Intent> mAddEditMedicamentoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("MainActivity", "Resultado recebido da tela de adicionar/editar - Código: " + result.getResultCode());
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    int id = data.getIntExtra(AddEditMedicamentoActivity.EXTRA_REPLY_ID, -1);

                    String nome = data.getStringExtra(AddEditMedicamentoActivity.EXTRA_REPLY_NOME);
                    String descricao = data.getStringExtra(AddEditMedicamentoActivity.EXTRA_REPLY_DESCRICAO);
                    String dose = data.getStringExtra(AddEditMedicamentoActivity.EXTRA_REPLY_DOSE);
                    String imagemUri = data.getStringExtra(AddEditMedicamentoActivity.EXTRA_REPLY_IMAGEM_URI);
                    int estoqueAtual = data.getIntExtra(AddEditMedicamentoActivity.EXTRA_REPLY_ESTOQUE_ATUAL, 0);
                    int estoqueMinimo = data.getIntExtra(AddEditMedicamentoActivity.EXTRA_REPLY_ESTOQUE_MINIMO, 0);
                    String tipo = data.getStringExtra(AddEditMedicamentoActivity.EXTRA_REPLY_TIPO);

                    Log.d("MainActivity", "Dados recebidos - ID: " + id + ", Nome: " + nome + 
                          ", Dose: " + dose + ", Estoque: " + estoqueAtual + "/" + estoqueMinimo);

                    Medicamento medicamento = new Medicamento();
                    medicamento.nome = nome;
                    medicamento.descricao = descricao;
                    medicamento.dose = dose;
                    medicamento.imagem = imagemUri;
                    medicamento.estoque_atual = estoqueAtual;
                    medicamento.estoque_minimo = estoqueMinimo;
                    medicamento.tipo = tipo;

                    if (id == -1) {
                        Log.d("MainActivity", "Operação: CRIAR novo medicamento");
                        // NOVO MEDICAMENTO: usa o save com callback para obter o ID e navegar
                        mMedicamentoViewModel.save(medicamento, (long newId) -> {
                            Log.d("MainActivity", "Medicamento criado com ID: " + newId + " - navegando para tela de horário");
                            // Este código é executado em background após o medicamento ser salvo
                            // Para interagir com a UI, precisamos garantir que está na thread principal
                            runOnUiThread(() -> {
                                Intent intent = new Intent(MainActivity.this, HorarioActivity.class);
                                intent.putExtra(HorarioActivity.EXTRA_MEDICAMENTO_ID, (int) newId);
                                intent.putExtra(HorarioActivity.EXTRA_MEDICAMENTO_NOME, medicamento.nome);
                                startActivity(intent);
                            });
                        });
                        Toast.makeText(this, "Medicamento criado.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d("MainActivity", "Operação: ATUALIZAR medicamento existente ID: " + id);
                        // ATUALIZAÇÃO: usa o save normal, sem navegar
                        medicamento.id = id;
                        mMedicamentoViewModel.save(medicamento);
                        Toast.makeText(this, "Medicamento atualizado.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("MainActivity", "Operação cancelada pelo usuário");
                    Toast.makeText(this, "Operação cancelada.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "onCreate() iniciado");
        setContentView(R.layout.activity_main);
        
        Log.d("MainActivity", "Criando canal de notificação...");
        NotificationHelper.createNotificationChannel(this);

        Log.d("MainActivity", "Solicitando permissão de notificação...");
        askNotificationPermission();

        Log.d("MainActivity", "Configurando RecyclerView e Adapter...");
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final MedicamentoListAdapter adapter = new MedicamentoListAdapter(new MedicamentoListAdapter.MedicamentoDiff());
        recyclerView.setAdapter(adapter);

        Log.d("MainActivity", "Inicializando ViewModel...");
        mMedicamentoViewModel = new ViewModelProvider(this).get(MedicamentoViewModel.class);

        Log.d("MainActivity", "Configurando observador dos medicamentos com horários...");
        mMedicamentoViewModel.getAllMedicamentosComHorarios().observe(this, medicamentosComHorarios -> {
            Log.d("MainActivity", "Lista de medicamentos com horários atualizada. Total: " + 
                  (medicamentosComHorarios != null ? medicamentosComHorarios.size() : 0));
            adapter.submitList(medicamentosComHorarios);
            
            // Verifica estoque baixo quando a lista é atualizada
            if (medicamentosComHorarios != null) {
                java.util.List<com.example.alarmed.data.db.entity.Medicamento> medicamentos = 
                    new java.util.ArrayList<>();
                for (com.example.alarmed.data.db.relacionamentos.MedicamentoComHorarios mch : medicamentosComHorarios) {
                    medicamentos.add(mch.medicamento);
                }
                checkLowStockForAllMedications(medicamentos);
            }
        });

        // O FAB abre a a tela pra adc novo medicamento
        Log.d("MainActivity", "Configurando FAB...");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Log.d("MainActivity", "FAB clicado - abrindo tela de novo medicamento");
            Intent intent = new Intent(this, AddEditMedicamentoActivity.class);
            mAddEditMedicamentoLauncher.launch(intent);
        });

        // Listener para o clique em um item da lista (para edição)
        Log.d("MainActivity", "Configurando listener de clique dos itens...");
        adapter.setOnItemClickListener(medicamento -> {
            Log.d("MainActivity", "Item clicado - editando medicamento ID: " + medicamento.id + 
                  ", Nome: " + medicamento.nome);
            Intent intent = new Intent(this, AddEditMedicamentoActivity.class);
            intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_ID, medicamento.id);
            Log.i("MainActivity", "id sent in extra to NewMedicamentoActivity intent: " + medicamento.id);
            intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_NOME, medicamento.nome);
            intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_DESCRICAO, medicamento.descricao);
            intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_DOSE, medicamento.dose);
            intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_IMAGEM_URI, medicamento.imagem);
            intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_ESTOQUE_ATUAL, medicamento.estoque_atual);
            intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_ESTOQUE_MINIMO, medicamento.estoque_minimo);
            intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_TIPO, medicamento.tipo);
            mAddEditMedicamentoLauncher.launch(intent);
        });

        // Listener para os botões dos cards
        adapter.setOnButtonClickListener(new MedicamentoListAdapter.OnButtonClickListener() {
            @Override
            public void onTomeiClick(Medicamento medicamento) {
                Log.d("MainActivity", "Botão 'Tomei' clicado para medicamento: " + medicamento.nome);
                StockManager stockManager = new StockManager(MainActivity.this);
                stockManager.medicamentTaken(medicamento.id);
                Toast.makeText(MainActivity.this, "Medicamento tomado registrado", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(Medicamento medicamento) {
                Log.d("MainActivity", "Botão editar clicado - medicamento ID: " + medicamento.id);
                Intent intent = new Intent(MainActivity.this, AddEditMedicamentoActivity.class);
                intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_ID, medicamento.id);
                intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_NOME, medicamento.nome);
                intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_DESCRICAO, medicamento.descricao);
                intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_DOSE, medicamento.dose);
                intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_IMAGEM_URI, medicamento.imagem);
                intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_ESTOQUE_ATUAL, medicamento.estoque_atual);
                intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_ESTOQUE_MINIMO, medicamento.estoque_minimo);
                intent.putExtra(AddEditMedicamentoActivity.EXTRA_REPLY_TIPO, medicamento.tipo);
                mAddEditMedicamentoLauncher.launch(intent);
            }

            @Override
            public void onDeleteClick(Medicamento medicamento) {
                Log.d("MainActivity", "Botão excluir clicado - medicamento ID: " + medicamento.id);
                mMedicamentoViewModel.deleteById(medicamento.id);
                Toast.makeText(MainActivity.this, "Medicamento excluído", Toast.LENGTH_SHORT).show();
            }
        });

        // Configurar botões inferiores
        Log.d("MainActivity", "Configurando botões inferiores...");
        com.google.android.material.button.MaterialButton btnVerHistorico = findViewById(R.id.btnVerHistorico);
        com.google.android.material.button.MaterialButton btnGerarPdf = findViewById(R.id.btnGerarPdf);

        btnVerHistorico.setOnClickListener(view -> {
            Log.d("MainActivity", "Botão 'Ver Histórico' clicado");
            Intent intent = new Intent(MainActivity.this, HistoricoActivity.class);
            startActivity(intent);
        });

        btnGerarPdf.setOnClickListener(view -> {
            Log.d("MainActivity", "Botão 'Gerar PDF' clicado");
            Toast.makeText(MainActivity.this, "Funcionalidade de PDF será implementada em breve", Toast.LENGTH_SHORT).show();
        });

        // A funcionalidade de deletar
        Log.d("MainActivity", "Configurando swipe para deletar...");
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Medicamento med = adapter.getMedicamentoAt(position);
                Log.d("MainActivity", "Medicamento deletado via swipe - ID: " + med.id + ", Nome: " + med.nome);
                mMedicamentoViewModel.deleteById(med.id);
                Toast.makeText(MainActivity.this, "Medicamento deletado", Toast.LENGTH_SHORT).show();
            }

        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
        Log.d("MainActivity", "onCreate() finalizado");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Testar Notificação");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            // Testa a notificação
            Log.d("MainActivity", "Testando notificação...");
            NotificationHelper.testNotification(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Implementação do método para pedir permissão
    private void askNotificationPermission() {
        Log.d("MainActivity", "askNotificationPermission() iniciado");
        // Obrigatório a partir da API 33 (Android 13)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("MainActivity", "Android 13+ detectado, verificando permissão POST_NOTIFICATIONS");
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Permissão não concedida, solicitando...");
                // Lança o pedido de permissão
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            } else {
                Log.d("MainActivity", "Permissão já concedida");
            }
        } else {
            Log.d("MainActivity", "Android < 13, permissão não necessária");
        }
    }

    /**
     * Verifica estoque baixo para todos os medicamentos
     */
    private void checkLowStockForAllMedications(java.util.List<Medicamento> medicamentos) {
        if (medicamentos == null) return;
        
        Log.d("MainActivity", "Verificando estoque baixo para " + medicamentos.size() + " medicamentos");
        
        for (Medicamento medicamento : medicamentos) {
            if (NotificationHelper.isLowStock(medicamento)) {
                Log.w("MainActivity", "Estoque baixo detectado: " + medicamento.nome + 
                      " (Atual: " + medicamento.estoque_atual + ", Mínimo: " + medicamento.estoque_minimo + ")");
                NotificationHelper.sendLowStockNotification(this, medicamento);
            }
        }
    }
}
