package com.example.alarmed.ui.medicamentos.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmed.R;
import com.example.alarmed.data.repos.MedicamentoRepository;

/**
 * Activity para visualização de medicamentos (somente leitura).
 * Usuários comuns podem apenas visualizar as informações.
 * Segue o Princípio da Responsabilidade Única.
 */
public class ViewMedicamentoActivity extends AppCompatActivity {

    private static final String TAG = "ViewMedicamentoActivity";
    
    // Chaves para os dados recebidos no Intent
    public static final String EXTRA_MEDICAMENTO_ID = "com.example.android.medicamento.VIEW_ID";
    public static final String EXTRA_MEDICAMENTO_NOME = "com.example.android.medicamento.VIEW_NOME";
    public static final String EXTRA_MEDICAMENTO_DESCRICAO = "com.example.android.medicamento.VIEW_DESCRICAO";
    public static final String EXTRA_MEDICAMENTO_DOSE = "com.example.android.medicamento.VIEW_DOSE";
    public static final String EXTRA_MEDICAMENTO_IMAGEM_URI = "com.example.android.medicamento.VIEW_IMAGEM_URI";
    public static final String EXTRA_MEDICAMENTO_ESTOQUE_ATUAL = "com.example.android.medicamento.VIEW_ESTOQUE_ATUAL";
    public static final String EXTRA_MEDICAMENTO_ESTOQUE_MINIMO = "com.example.android.medicamento.VIEW_ESTOQUE_MINIMO";
    public static final String EXTRA_MEDICAMENTO_TIPO = "com.example.android.medicamento.VIEW_TIPO";

    private TextView textNome;
    private TextView textTipo;
    private TextView textDose;
    private TextView textDescricao;
    private TextView textEstoqueAtual;
    private TextView textEstoqueMinimo;
    private ImageView imageMedicamento;
    private Button buttonConsultarHorario;
    private Button buttonFechar;
    
    private int medicamentoId;
    private MedicamentoRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() iniciado");
        setContentView(R.layout.activity_view_medicamento);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Visualizar Medicamento");
        }

        // Inicializar views
        initializeViews();
        
        // Inicializar repository
        repository = new MedicamentoRepository(getApplication());

        // Obter dados do Intent
        loadMedicamentoData();

        // Configurar listeners
        setupListeners();
        
        Log.d(TAG, "onCreate() finalizado");
    }

    /**
     * Inicializa as views da activity.
     */
    private void initializeViews() {
        textNome = findViewById(R.id.text_nome);
        textTipo = findViewById(R.id.text_tipo);
        textDose = findViewById(R.id.text_dose);
        textDescricao = findViewById(R.id.text_descricao);
        textEstoqueAtual = findViewById(R.id.text_estoque_atual);
        textEstoqueMinimo = findViewById(R.id.text_estoque_minimo);
        imageMedicamento = findViewById(R.id.image_medicamento);
        buttonConsultarHorario = findViewById(R.id.button_consultar_horario);
        buttonFechar = findViewById(R.id.button_fechar);
    }

    /**
     * Carrega os dados do medicamento do Intent.
     */
    private void loadMedicamentoData() {
        Intent intent = getIntent();
        
        medicamentoId = intent.getIntExtra(EXTRA_MEDICAMENTO_ID, -1);
        String nome = intent.getStringExtra(EXTRA_MEDICAMENTO_NOME);
        String tipo = intent.getStringExtra(EXTRA_MEDICAMENTO_TIPO);
        String dose = intent.getStringExtra(EXTRA_MEDICAMENTO_DOSE);
        String descricao = intent.getStringExtra(EXTRA_MEDICAMENTO_DESCRICAO);
        int estoqueAtual = intent.getIntExtra(EXTRA_MEDICAMENTO_ESTOQUE_ATUAL, 0);
        int estoqueMinimo = intent.getIntExtra(EXTRA_MEDICAMENTO_ESTOQUE_MINIMO, 0);
        String imagemUri = intent.getStringExtra(EXTRA_MEDICAMENTO_IMAGEM_URI);

        Log.d(TAG, "Carregando dados do medicamento ID: " + medicamentoId + ", Nome: " + nome);

        // Preencher as views
        textNome.setText(nome != null ? nome : "Não informado");
        textTipo.setText(tipo != null ? tipo : "Não informado");
        textDose.setText(dose != null && !dose.isEmpty() ? dose : "Não informado");
        textDescricao.setText(descricao != null && !descricao.isEmpty() ? descricao : "Não informado");
        textEstoqueAtual.setText(String.valueOf(estoqueAtual));
        textEstoqueMinimo.setText(String.valueOf(estoqueMinimo));

        // Carregar imagem se disponível
        if (imagemUri != null && !imagemUri.isEmpty()) {
            Log.d(TAG, "Carregando imagem: " + imagemUri);
            imageMedicamento.setImageURI(Uri.parse(imagemUri));
        } else {
            imageMedicamento.setImageResource(R.drawable.ic_placeholder_image);
        }
    }

    /**
     * Configura os listeners dos botões.
     */
    private void setupListeners() {
        buttonConsultarHorario.setOnClickListener(v -> consultarHorario());
        buttonFechar.setOnClickListener(v -> finish());
    }

    /**
     * Consulta e exibe o horário cadastrado para o medicamento.
     */
    private void consultarHorario() {
        Log.d(TAG, "Consultando horário para medicamento ID: " + medicamentoId);
        
        if (medicamentoId == -1) {
            Toast.makeText(this, "Erro: ID do medicamento não encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        repository.getHorarioParaMedicamento(medicamentoId).observe(this, horario -> {
            if (horario != null) {
                Log.d(TAG, "Horário encontrado - Inicial: " + horario.horario_inicial + 
                      ", Intervalo: " + horario.intervalo);
                      
                String mensagem = String.format(
                    "Horário cadastrado:\n\n" +
                    "⏰ Primeira dose: %s\n" +
                    "🔄 Intervalo: %d horas\n",
                    horario.horario_inicial,
                    horario.intervalo);

                new AlertDialog.Builder(this)
                    .setTitle("Informações do Horário")
                    .setMessage(mensagem)
                    .setPositiveButton("OK", null)
                    .show();
            } else {
                Log.d(TAG, "Nenhum horário encontrado");
                new AlertDialog.Builder(this)
                    .setTitle("Informação")
                    .setMessage("Nenhum horário cadastrado para este medicamento.\n\n" +
                               "Um administrador pode configurar o horário na tela de edição.")
                    .setPositiveButton("OK", null)
                    .show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
