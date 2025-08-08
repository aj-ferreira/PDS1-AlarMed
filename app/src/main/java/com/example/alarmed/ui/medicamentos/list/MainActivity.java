package com.example.alarmed.ui.medicamentos.list;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.alarmed.ui.medicamentos.detail.MedicamentoViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private MedicamentoViewModel mMedicamentoViewModel;

    // O novo método recomendado para obter o resultado de uma activity.
    private final ActivityResultLauncher<Intent> mNewMedicamentoActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    // Extrai todos os dados do Intent
                    String nome = data.getStringExtra(com.example.alarmed.ui.medicamentos.addedit.NewMedicamentoActivity.EXTRA_REPLY_NOME);
                    String descricao = data.getStringExtra(com.example.alarmed.ui.medicamentos.addedit.NewMedicamentoActivity.EXTRA_REPLY_DESCRICAO);
                    String imagemUri = data.getStringExtra(com.example.alarmed.ui.medicamentos.addedit.NewMedicamentoActivity.EXTRA_REPLY_IMAGEM_URI);
                    int estoqueAtual = data.getIntExtra(com.example.alarmed.ui.medicamentos.addedit.NewMedicamentoActivity.EXTRA_REPLY_ESTOQUE_ATUAL, 0);
                    int estoqueMinimo = data.getIntExtra(com.example.alarmed.ui.medicamentos.addedit.NewMedicamentoActivity.EXTRA_REPLY_ESTOQUE_MINIMO, 0);
                    String tipo = data.getStringExtra(com.example.alarmed.ui.medicamentos.addedit.NewMedicamentoActivity.EXTRA_REPLY_TIPO);

                    // Cria o objeto Medicamento com todos os campos
                    Medicamento medicamento = new Medicamento();
                    medicamento.nome = nome;
                    medicamento.descricao = descricao;
                    medicamento.imagem = imagemUri;
                    medicamento.estoque_atual = estoqueAtual;
                    medicamento.estoque_minimo = estoqueMinimo;
                    medicamento.tipo = tipo;

                    mMedicamentoViewModel.insert(medicamento);
                    Toast.makeText(this, "Medicamento salvo.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Medicamento não salvo.", Toast.LENGTH_SHORT).show();
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

        mMedicamentoViewModel.getAllMedicamentos().observe(this, medicamentos -> {
            adapter.submitList(medicamentos);
        });

        // O FAB agora abre a nova activity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, com.example.alarmed.ui.medicamentos.addedit.NewMedicamentoActivity.class);
            mNewMedicamentoActivityLauncher.launch(intent);
        });

        // A funcionalidade de deletar permanece a mesma
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
