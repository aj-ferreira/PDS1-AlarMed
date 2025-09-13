package com.example.alarmed.ui.historico;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmed.R;
import com.example.alarmed.ui.medicamentos.detail.MedicamentoViewModel;

public class HistoricoActivity extends AppCompatActivity {

    private static final String TAG = "HistoricoActivity";
    private MedicamentoViewModel mMedicamentoViewModel;
    private HistoricoAdapter mAdapter;
    private android.widget.LinearLayout layoutSemHistorico;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() iniciado");
        setContentView(R.layout.activity_historico);

        // Configurar a ActionBar com botão de voltar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Histórico de Medicamentos");
        }

        // Configurar RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewHistorico);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        mAdapter = new HistoricoAdapter();
        recyclerView.setAdapter(mAdapter);

        // Obter referência do layout de "sem histórico"
        layoutSemHistorico = findViewById(R.id.layoutSemHistorico);

        // Configurar ViewModel
        mMedicamentoViewModel = new ViewModelProvider(this).get(MedicamentoViewModel.class);

        // Observar todos os históricos
        mMedicamentoViewModel.getAllHistorico().observe(this, historicos -> {
            Log.d(TAG, "Lista de históricos atualizada. Total: " + 
                  (historicos != null ? historicos.size() : 0));
            mAdapter.submitList(historicos);
            
            // Mostrar/esconder mensagem de "sem histórico"
            if (historicos == null || historicos.isEmpty()) {
                layoutSemHistorico.setVisibility(android.view.View.VISIBLE);
                recyclerView.setVisibility(android.view.View.GONE);
            } else {
                layoutSemHistorico.setVisibility(android.view.View.GONE);
                recyclerView.setVisibility(android.view.View.VISIBLE);
            }
        });

        Log.d(TAG, "onCreate() finalizado");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Botão de voltar pressionado
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
