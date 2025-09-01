package com.example.alarmed.ui.medicamentos.list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmed.R;
import com.example.alarmed.data.db.entity.Medicamento;
import com.example.alarmed.ui.medicamentos.addedit.NewMedicamentoActivity;
import com.example.alarmed.ui.medicamentos.detail.MedicamentoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private MedicamentoViewModel mMedicamentoViewModel;

    // Launcher para a tela de Adicionar/Editar
    private final ActivityResultLauncher<Intent> mAddEditMedicamentoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    int id = data.getIntExtra(NewMedicamentoActivity.EXTRA_REPLY_ID, -1); //-1 é o valor retornado caso não haja nenhum associado ao extra
                    Log.i("MainActivity", "id in method startSctivityForResult: " + id);
                    // Extrai todos os dados do Intent
                    String nome = data.getStringExtra(NewMedicamentoActivity.EXTRA_REPLY_NOME);
                    String descricao = data.getStringExtra(NewMedicamentoActivity.EXTRA_REPLY_DESCRICAO);
                    String imagemUri = data.getStringExtra(NewMedicamentoActivity.EXTRA_REPLY_IMAGEM_URI);
                    int estoqueAtual = data.getIntExtra(NewMedicamentoActivity.EXTRA_REPLY_ESTOQUE_ATUAL, 0);
                    int estoqueMinimo = data.getIntExtra(NewMedicamentoActivity.EXTRA_REPLY_ESTOQUE_MINIMO, 0);
                    String tipo = data.getStringExtra(NewMedicamentoActivity.EXTRA_REPLY_TIPO);

                    // Cria o objeto Medicamento com todos os campos
                    Medicamento medicamento = new Medicamento();
                    medicamento.nome = nome;
                    medicamento.descricao = descricao;
                    medicamento.imagem = imagemUri;
                    medicamento.estoque_atual = estoqueAtual;
                    medicamento.estoque_minimo = estoqueMinimo;
                    medicamento.tipo = tipo;

                    //Apenas definimos o ID se for um item existente (edição).
                    // Se for um item novo, o ID permanece 0, e o Room irá gerar um novo.
                    if (id != -1) {
                        medicamento.id = id;
                    }


                    // Usamos o método save para ambas as operações.
                    mMedicamentoViewModel.save(medicamento);

                    if(id == -1){
                        Toast.makeText(this, "Medicamento salvo.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, "Medicamento atualizado.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Operação cancelada.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final MedicamentoListAdapter adapter = new MedicamentoListAdapter(new MedicamentoListAdapter.MedicamentoDiff());
        recyclerView.setAdapter(adapter);

        mMedicamentoViewModel = new ViewModelProvider(this).get(MedicamentoViewModel.class);

        mMedicamentoViewModel.getAllMedicamentos().observe(this,adapter::submitList);

        // O FAB abre a a tela pra adc novo medicamento
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, NewMedicamentoActivity.class);
            mAddEditMedicamentoLauncher.launch(intent);
        });

        // Listener para o clique em um item da lista (para edição)
        adapter.setOnItemClickListener(medicamento -> {
            Intent intent = new Intent(this, NewMedicamentoActivity.class);
            intent.putExtra(NewMedicamentoActivity.EXTRA_REPLY_ID, medicamento.id);
            Log.i("MainActivity", "id sent in extra to NewMedicamentoActivity intent: " + medicamento.id);
            intent.putExtra(NewMedicamentoActivity.EXTRA_REPLY_NOME, medicamento.nome);
            intent.putExtra(NewMedicamentoActivity.EXTRA_REPLY_DESCRICAO, medicamento.descricao);
            intent.putExtra(NewMedicamentoActivity.EXTRA_REPLY_IMAGEM_URI, medicamento.imagem);
            intent.putExtra(NewMedicamentoActivity.EXTRA_REPLY_ESTOQUE_ATUAL, medicamento.estoque_atual);
            intent.putExtra(NewMedicamentoActivity.EXTRA_REPLY_ESTOQUE_MINIMO, medicamento.estoque_minimo);
            intent.putExtra(NewMedicamentoActivity.EXTRA_REPLY_TIPO, medicamento.tipo);
            mAddEditMedicamentoLauncher.launch(intent);
        });

        // A funcionalidade de deletar
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
                mMedicamentoViewModel.deleteById(med.id);
                Toast.makeText(MainActivity.this, "Medicamento deletado", Toast.LENGTH_SHORT).show();
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
