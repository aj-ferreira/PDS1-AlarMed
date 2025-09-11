package com.example.alarmed.ui.medicamentos.addedit;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmed.R;
import com.example.alarmed.data.repos.MedicamentoRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class AddEditMedicamentoActivity extends AppCompatActivity {

    // Chaves para os dados retornados no Intent
    public static final String EXTRA_REPLY_NOME = "com.example.android.medicamento.REPLY_NOME";
    public static final String EXTRA_REPLY_DESCRICAO = "com.example.android.medicamento.REPLY_DESCRICAO";
    public static final String EXTRA_REPLY_IMAGEM_URI = "com.example.android.medicamento.REPLY_IMAGEM_URI";
    public static final String EXTRA_REPLY_ESTOQUE_ATUAL = "com.example.android.medicamento.REPLY_ESTOQUE_ATUAL";
    public static final String EXTRA_REPLY_ESTOQUE_MINIMO = "com.example.android.medicamento.REPLY_ESTOQUE_MINIMO";
    public static final String EXTRA_REPLY_TIPO = "com.example.android.medicamento.REPLY_TIPO";
    public static final String EXTRA_REPLY_DOSE = "com.example.android.medicamento.REPLY_DOSE";
    public static final String EXTRA_REPLY_ID = "com.example.android.medicamento.REPLY_ID";

    private EditText mEditNomeView;
    private EditText mEditDescricaoView;
    private EditText mEditDoseView;
    private EditText mEditEstoqueAtualView;
    private EditText mEditEstoqueMinimoView;
    private Spinner mSpinnerTipo;
    private ImageView mImagePreview;
    private String mImagemUriString = "";
    private String mCopiedImagePath = null;
    private int mId = -1; // -1 indica um novo medicamento
    private MedicamentoRepository mRepository;

    // Launcher para o seletor de imagens
//    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
//            new ActivityResultContracts.GetContent(),
//            uri -> {
//                if (uri != null) {
//                    mImagePreview.setImageURI(uri);
//                    mImagemUriString = uri.toString();
//                }
//            });
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AddEditMedicamentoActivity", "onCreate() iniciado");
        setContentView(R.layout.activity_new_medicamento);

        // Encontra as views do layout
        Log.d("AddEditMedicamentoActivity", "Inicializando views...");
        mEditNomeView = findViewById(R.id.edit_nome);
        mEditDescricaoView = findViewById(R.id.edit_descricao);
        mEditDoseView = findViewById(R.id.edit_dose);
        mEditEstoqueAtualView = findViewById(R.id.edit_estoque_atual);
        mEditEstoqueMinimoView = findViewById(R.id.edit_estoque_minimo);
        mSpinnerTipo = findViewById(R.id.spinner_tipo);
        mImagePreview = findViewById(R.id.image_preview);
        Button buttonSelectImage = findViewById(R.id.button_selecionar_imagem);
        Button buttonConsultarHorario = findViewById(R.id.button_consultar_horario);
        final Button buttonSave = findViewById(R.id.button_save);

        // Inicializa o repositório
        Log.d("AddEditMedicamentoActivity", "Inicializando repositório...");
        mRepository = new MedicamentoRepository(getApplication());

        Log.d("AddEditMedicamentoActivity", "Configurando picker de imagem...");
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts
                .PickVisualMedia(), uri -> {
            Log.d("AddEditMedicamentoActivity", "Imagem selecionada: " + (uri != null ? uri.toString() : "null"));
            if (uri != null) {
                Log.d("NewMedicamentoActivity", "Selected URI: " + uri.toString());
                mImagePreview.setImageURI(uri);
                copyImageToInternalStorage(uri);
            } else {
                Log.d("NewMedicamentoActivity", "No media selected");
                mCopiedImagePath = null;
                mImagePreview.setImageResource(R.drawable.ic_placeholder_image);
            }
        });
        buttonSelectImage.setOnClickListener(view -> {
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });


        // Configura o Spinner com os tipos de medicamento
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_medicamento, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerTipo.setAdapter(adapter);

        // Verifica se está em modo de edição e preenche os campos
        final Intent intent = getIntent();
        Log.d("AddEditMedicamentoActivity", "Verificando modo de operação...");
        if (intent.hasExtra(EXTRA_REPLY_ID)) {
            Log.i("AddEditMedicamentoActivity", "Modo de edição ativado");
            Log.i("NewMedicamentoActivity", "Modo de edição ativado com EXTRA_REPLY_ID =" + EXTRA_REPLY_ID);
            setTitle("Editar Medicamento");
            mId = intent.getIntExtra(EXTRA_REPLY_ID, -1);
            Log.i("AddEditMedicamentoActivity", "ID do medicamento: " + mId);
            Log.i("NewMedicamentoActivity", "mId value: " + mId);
            
            // Mostra o botão de consultar horário apenas no modo de edição
            buttonConsultarHorario.setVisibility(Button.VISIBLE);
            Log.d("AddEditMedicamentoActivity", "Botão consultar horário habilitado");
            
            // Preenchendo campos com dados existentes
            Log.d("AddEditMedicamentoActivity", "Preenchendo campos com dados existentes...");
            mEditNomeView.setText(intent.getStringExtra(EXTRA_REPLY_NOME));
            mEditDescricaoView.setText(intent.getStringExtra(EXTRA_REPLY_DESCRICAO));
            mEditDoseView.setText(intent.getStringExtra(EXTRA_REPLY_DOSE));
            mEditEstoqueAtualView.setText(String.valueOf(intent.getIntExtra(EXTRA_REPLY_ESTOQUE_ATUAL, 0)));
            mEditEstoqueMinimoView.setText(String.valueOf(intent.getIntExtra(EXTRA_REPLY_ESTOQUE_MINIMO, 0)));
            mImagemUriString = intent.getStringExtra(EXTRA_REPLY_IMAGEM_URI);
            if (mImagemUriString != null && !mImagemUriString.isEmpty()) {
                Log.d("AddEditMedicamentoActivity", "Carregando imagem existente: " + mImagemUriString);
                mImagePreview.setImageURI(Uri.parse(mImagemUriString));
            }
            // Seleciona o item correto no Spinner
            String tipo = intent.getStringExtra(EXTRA_REPLY_TIPO);
            if (tipo != null) {
                int spinnerPosition = adapter.getPosition(tipo);
                mSpinnerTipo.setSelection(spinnerPosition);
                Log.d("AddEditMedicamentoActivity", "Tipo selecionado: " + tipo + " (posição: " + spinnerPosition + ")");
            }
        } else {
            Log.i("AddEditMedicamentoActivity", "Modo de criação ativado");
            setTitle("Adicionar Medicamento");
            // Esconde o botão de consultar horário no modo de criação
            buttonConsultarHorario.setVisibility(Button.GONE);
            Log.d("AddEditMedicamentoActivity", "Botão consultar horário desabilitado");
        }

        // Listener para o botão de selecionar imagem
        //buttonSelectImage.setOnClickListener(view -> mGetContent.launch("image/*"));

        // Listener para o botão de consultar horário
        buttonConsultarHorario.setOnClickListener(view -> consultarHorario());

        // Listener para o botão de salvar
        buttonSave.setOnClickListener(view -> {
            Log.d("AddEditMedicamentoActivity", "Botão salvar clicado");
            Intent replyIntent = new Intent();
            // Validação dos campos obrigatórios
            if (TextUtils.isEmpty(mEditNomeView.getText()) ||
                    TextUtils.isEmpty(mEditEstoqueAtualView.getText()) ||
                    TextUtils.isEmpty(mEditEstoqueMinimoView.getText())) {
                Log.w("AddEditMedicamentoActivity", "Campos obrigatórios não preenchidos");
                Toast.makeText(this, "Preencha todos os campos obrigatórios (*)", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                // Coleta os dados de todas as views
                Log.d("AddEditMedicamentoActivity", "Coletando dados dos campos...");
                String nome = mEditNomeView.getText().toString();
                String descricao = mEditDescricaoView.getText().toString();
                String dose = mEditDoseView.getText().toString();
                int estoqueAtual = Integer.parseInt(mEditEstoqueAtualView.getText().toString());
                int estoqueMinimo = Integer.parseInt(mEditEstoqueMinimoView.getText().toString());
                String tipo = mSpinnerTipo.getSelectedItem().toString();

                Log.d("AddEditMedicamentoActivity", "Dados coletados - Nome: " + nome + 
                      ", Dose: " + dose + ", Estoque: " + estoqueAtual + "/" + estoqueMinimo + 
                      ", Tipo: " + tipo);

                // Coloca os dados no Intent de resposta
                replyIntent.putExtra(EXTRA_REPLY_ID, mId);
                replyIntent.putExtra(EXTRA_REPLY_NOME, nome);
                replyIntent.putExtra(EXTRA_REPLY_DESCRICAO, descricao);
                replyIntent.putExtra(EXTRA_REPLY_DOSE, dose);
                replyIntent.putExtra(EXTRA_REPLY_IMAGEM_URI, mImagemUriString);
                replyIntent.putExtra(EXTRA_REPLY_ESTOQUE_ATUAL, estoqueAtual);
                replyIntent.putExtra(EXTRA_REPLY_ESTOQUE_MINIMO, estoqueMinimo);
                replyIntent.putExtra(EXTRA_REPLY_TIPO, tipo);

                Log.d("AddEditMedicamentoActivity", "Intent de resposta preparado, finalizando activity");
                setResult(RESULT_OK, replyIntent);
                finish(); // Fecha a activity e retorna para a MainActivity
            }
        });
        
        Log.d("AddEditMedicamentoActivity", "onCreate() finalizado");
    }

    private void copyImageToInternalStorage(Uri uri) {
        Log.d("AddEditMedicamentoActivity", "Iniciando cópia de imagem para storage interno: " + uri);
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            ContentResolver contentResolver = getContentResolver();
            inputStream = contentResolver.openInputStream(uri);

            File imageDir = new File(getFilesDir(), "medicamento_images");
            if (!imageDir.exists()) {
                Log.d("AddEditMedicamentoActivity", "Criando diretório de imagens: " + imageDir.getAbsolutePath());
                imageDir.mkdirs();
            }
            File outputFile = new File(imageDir, "img_" + System.currentTimeMillis() + ".jpg");
            outputStream = new FileOutputStream(outputFile);

            if (inputStream != null) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, len);
                }
                mCopiedImagePath = outputFile.getAbsolutePath();
                mImagemUriString = mCopiedImagePath;
                Log.d("AddEditMedicamentoActivity", "Imagem copiada com sucesso para: " + mCopiedImagePath);
                Log.d("NewMedicamentoActivity", "Image copied to: " + mCopiedImagePath);
            } else {
                Log.e("AddEditMedicamentoActivity", "InputStream é null");
                mCopiedImagePath = null;
                mImagemUriString = ""; // Limpa também em caso de erro
            }
        } catch (IOException e) {
            Log.e("AddEditMedicamentoActivity", "Erro ao copiar imagem: " + e.getMessage(), e);
            Log.e("NewMedicamentoActivity", "Error copying image", e);
            mCopiedImagePath = null;
            mImagemUriString = ""; // Limpa também em caso de erro
            Toast.makeText(this, "Erro ao salvar imagem.", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                Log.d("AddEditMedicamentoActivity", "Streams fechados");
            } catch (IOException e) {
                Log.e("AddEditMedicamentoActivity", "Erro ao fechar streams: " + e.getMessage(), e);
                Log.e("NewMedicamentoActivity", "Error closing streams", e);
            }
        }
    }

    /**
     * Consulta e exibe o horário cadastrado para o medicamento em edição
     */
    private void consultarHorario() {
        Log.d("AddEditMedicamentoActivity", "consultarHorario() iniciado");
        if (mId == -1) {
            Log.w("AddEditMedicamentoActivity", "ID do medicamento inválido: " + mId);
            Toast.makeText(this, "Erro: ID do medicamento não encontrado.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("AddEditMedicamentoActivity", "Buscando horário para medicamento ID: " + mId);
        mRepository.getHorarioParaMedicamento(mId).observe(this, horario -> {
            if (horario != null) {
                Log.d("AddEditMedicamentoActivity", "Horário encontrado - Inicial: " + horario.horario_inicial + 
                      ", Intervalo: " + horario.intervalo + ", Data fim: " + horario.dataFim);
                // Cria um dialog para mostrar as informações do horário
                String mensagem = String.format(
                    "Horário cadastrado:\n\n" +
                    "⏰ Primeira dose: %s\n" +
                    "🔄 Intervalo: %d horas\n" +
                    "📅 Data de fim: %s",
                    horario.horario_inicial,
                    horario.intervalo,
                    horario.dataFim != null && !horario.dataFim.isEmpty() ? horario.dataFim : "Indefinido"
                );

                Log.d("AddEditMedicamentoActivity", "Exibindo dialog com informações do horário");
                new AlertDialog.Builder(this)
                    .setTitle("Horário do Medicamento")
                    .setMessage(mensagem)
                    .setPositiveButton("OK", null)
                    .setNeutralButton("Editar Horário", (dialog, which) -> {
                        Log.d("AddEditMedicamentoActivity", "Usuário escolheu editar horário");
                        // Navega para a tela de horário
                        Intent intent = new Intent(this, com.example.alarmed.ui.horario.HorarioActivity.class);
                        intent.putExtra(com.example.alarmed.ui.horario.HorarioActivity.EXTRA_MEDICAMENTO_ID, mId);
                        intent.putExtra(com.example.alarmed.ui.horario.HorarioActivity.EXTRA_MEDICAMENTO_NOME, 
                                       mEditNomeView.getText().toString());
                        startActivity(intent);
                    })
                    .show();
            } else {
                Log.d("AddEditMedicamentoActivity", "Nenhum horário encontrado para o medicamento");
                // Não há horário cadastrado
                new AlertDialog.Builder(this)
                    .setTitle("Horário não encontrado")
                    .setMessage("Nenhum horário foi cadastrado para este medicamento.\n\nDeseja configurar um horário agora?")
                    .setPositiveButton("Sim", (dialog, which) -> {
                        Log.d("AddEditMedicamentoActivity", "Usuário escolheu configurar novo horário");
                        // Navega para a tela de horário
                        Intent intent = new Intent(this, com.example.alarmed.ui.horario.HorarioActivity.class);
                        intent.putExtra(com.example.alarmed.ui.horario.HorarioActivity.EXTRA_MEDICAMENTO_ID, mId);
                        intent.putExtra(com.example.alarmed.ui.horario.HorarioActivity.EXTRA_MEDICAMENTO_NOME, 
                                       mEditNomeView.getText().toString());
                        startActivity(intent);
                    })
                    .setNegativeButton("Não", (dialog, which) -> 
                        Log.d("AddEditMedicamentoActivity", "Usuário escolheu não configurar horário"))
                    .show();
            }
        });
        Log.d("AddEditMedicamentoActivity", "consultarHorario() finalizado");
    }

}
