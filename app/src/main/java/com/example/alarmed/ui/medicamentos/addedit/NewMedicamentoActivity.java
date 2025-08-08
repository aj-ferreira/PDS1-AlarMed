package com.example.alarmed.ui.medicamentos.addedit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

    private EditText mEditNomeView;
    private EditText mEditDescricaoView;
    private EditText mEditEstoqueAtualView;
    private EditText mEditEstoqueMinimoView;
    private Spinner mSpinnerTipo;
    private ImageView mImagePreview;
    private String mImagemUriString = "";

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
