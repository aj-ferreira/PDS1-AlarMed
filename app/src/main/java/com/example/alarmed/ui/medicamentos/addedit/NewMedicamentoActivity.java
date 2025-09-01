package com.example.alarmed.ui.medicamentos.addedit;

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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmed.R;


public class NewMedicamentoActivity extends AppCompatActivity {

    // Chaves para os dados retornados no Intent
    public static final String EXTRA_REPLY_NOME = "com.example.android.medicamento.REPLY_NOME";
    public static final String EXTRA_REPLY_DESCRICAO = "com.example.android.medicamento.REPLY_DESCRICAO";
    public static final String EXTRA_REPLY_IMAGEM_URI = "com.example.android.medicamento.REPLY_IMAGEM_URI";
    public static final String EXTRA_REPLY_ESTOQUE_ATUAL = "com.example.android.medicamento.REPLY_ESTOQUE_ATUAL";
    public static final String EXTRA_REPLY_ESTOQUE_MINIMO = "com.example.android.medicamento.REPLY_ESTOQUE_MINIMO";
    public static final String EXTRA_REPLY_TIPO = "com.example.android.medicamento.REPLY_TIPO";
    public static final String EXTRA_REPLY_ID = "com.example.android.medicamento.REPLY_ID";

    private EditText mEditNomeView;
    private EditText mEditDescricaoView;
    private EditText mEditEstoqueAtualView;
    private EditText mEditEstoqueMinimoView;
    private Spinner mSpinnerTipo;
    private ImageView mImagePreview;
    private String mImagemUriString = "";
    private int mId = -1; // -1 indica um novo medicamento

    // Launcher para o seletor de imagens
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    mImagePreview.setImageURI(uri);
                    mImagemUriString = uri.toString();
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_medicamento);

        // Encontra as views do layout
        mEditNomeView = findViewById(R.id.edit_nome);
        mEditDescricaoView = findViewById(R.id.edit_descricao);
        mEditEstoqueAtualView = findViewById(R.id.edit_estoque_atual);
        mEditEstoqueMinimoView = findViewById(R.id.edit_estoque_minimo);
        mSpinnerTipo = findViewById(R.id.spinner_tipo);
        mImagePreview = findViewById(R.id.image_preview);
        Button buttonSelectImage = findViewById(R.id.button_selecionar_imagem);
        final Button buttonSave = findViewById(R.id.button_save);

        // Configura o Spinner com os tipos de medicamento
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_medicamento, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerTipo.setAdapter(adapter);

        // Verifica se está em modo de edição e preenche os campos
        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_REPLY_ID)) {
            Log.i("NewMedicamentoActivity", "Modo de edição ativado com EXTRA_REPLY_ID =" + EXTRA_REPLY_ID);
            setTitle("Editar Medicamento");
            mId = intent.getIntExtra(EXTRA_REPLY_ID, -1);
            Log.i("NewMedicamentoActivity", "mId value: " + mId);
            mEditNomeView.setText(intent.getStringExtra(EXTRA_REPLY_NOME));
            mEditDescricaoView.setText(intent.getStringExtra(EXTRA_REPLY_DESCRICAO));
            mEditEstoqueAtualView.setText(String.valueOf(intent.getIntExtra(EXTRA_REPLY_ESTOQUE_ATUAL, 0)));
            mEditEstoqueMinimoView.setText(String.valueOf(intent.getIntExtra(EXTRA_REPLY_ESTOQUE_MINIMO, 0)));
            mImagemUriString = intent.getStringExtra(EXTRA_REPLY_IMAGEM_URI);
            if (mImagemUriString != null && !mImagemUriString.isEmpty()) {
                mImagePreview.setImageURI(Uri.parse(mImagemUriString));
            }
            // Seleciona o item correto no Spinner
            String tipo = intent.getStringExtra(EXTRA_REPLY_TIPO);
            if (tipo != null) {
                int spinnerPosition = adapter.getPosition(tipo);
                mSpinnerTipo.setSelection(spinnerPosition);
            }
        } else {
            setTitle("Adicionar Medicamento");
        }

        // Listener para o botão de selecionar imagem
        buttonSelectImage.setOnClickListener(view -> mGetContent.launch("image/*"));

        // Listener para o botão de salvar
        buttonSave.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            // Validação dos campos obrigatórios
            if (TextUtils.isEmpty(mEditNomeView.getText()) ||
                    TextUtils.isEmpty(mEditEstoqueAtualView.getText()) ||
                    TextUtils.isEmpty(mEditEstoqueMinimoView.getText())) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios (*)", Toast.LENGTH_LONG).show();
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                // Coleta os dados de todas as views
                String nome = mEditNomeView.getText().toString();
                String descricao = mEditDescricaoView.getText().toString();
                int estoqueAtual = Integer.parseInt(mEditEstoqueAtualView.getText().toString());
                int estoqueMinimo = Integer.parseInt(mEditEstoqueMinimoView.getText().toString());
                String tipo = mSpinnerTipo.getSelectedItem().toString();

                // Coloca os dados no Intent de resposta
                replyIntent.putExtra(EXTRA_REPLY_ID, mId);
                replyIntent.putExtra(EXTRA_REPLY_NOME, nome);
                replyIntent.putExtra(EXTRA_REPLY_DESCRICAO, descricao);
                replyIntent.putExtra(EXTRA_REPLY_IMAGEM_URI, mImagemUriString);
                replyIntent.putExtra(EXTRA_REPLY_ESTOQUE_ATUAL, estoqueAtual);
                replyIntent.putExtra(EXTRA_REPLY_ESTOQUE_MINIMO, estoqueMinimo);
                replyIntent.putExtra(EXTRA_REPLY_TIPO, tipo);

                setResult(RESULT_OK, replyIntent);
                finish(); // Fecha a activity e retorna para a MainActivity
            }
        });
    }
}
