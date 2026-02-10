package com.example.alarmed.ui.perfil;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmed.R;
import com.example.alarmed.data.db.entity.PerfilUsuario;
import com.example.alarmed.data.db.entity.Usuario;
import com.example.alarmed.data.repos.PerfilUsuarioRepository;
import com.example.alarmed.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Activity para exibir e editar perfil do usuário.
 * Admin pode editar, usuário comum apenas visualiza.
 * Segue o Princípio da Responsabilidade Única.
 */
public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = "PerfilActivity";

    // Views
    private TextView textNomeUsuario, textEmailUsuario;
    private TextView textImcValor, textImcClassificacao;
    private LinearLayout layoutImcInfo, layoutBotoesAcao;
    
    // Campos de entrada
    private TextInputEditText editDataNascimento, editTelefone, editEndereco;
    private TextInputEditText editCidade, editEstado, editCep;
    private TextInputEditText editPeso, editAltura;
    private AutoCompleteTextView editTipoSanguineo;
    private TextInputEditText editAlergias, editCondicoes, editMedicamentosContinuos;
    private TextInputEditText editContatoNome, editContatoTelefone, editContatoParentesco;
    private TextInputEditText editNomeMedico, editTelefoneMedico;
    private TextInputEditText editPlanoSaude, editNumeroCarteirinha, editObservacoes;
    
    // Botões
    private Button buttonSalvar, buttonCancelar;
    
    // Dados
    private SessionManager sessionManager;
    private PerfilUsuarioRepository perfilRepository;
    private Usuario usuarioAtual;
    private PerfilUsuario perfilAtual;
    private boolean modoEdicao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Perfil do Usuário");
        }

        // Inicializa componentes
        initializeComponents();
        setupListeners();
        loadUserProfile();
    }

    /**
     * Inicializa os componentes da view.
     */
    private void initializeComponents() {
        sessionManager = new SessionManager(this);
        perfilRepository = new PerfilUsuarioRepository(getApplication());

        // TextViews
        textNomeUsuario = findViewById(R.id.text_nome_usuario);
        textEmailUsuario = findViewById(R.id.text_email_usuario);
        textImcValor = findViewById(R.id.text_imc_valor);
        textImcClassificacao = findViewById(R.id.text_imc_classificacao);
        layoutImcInfo = findViewById(R.id.layout_imc_info);
        layoutBotoesAcao = findViewById(R.id.layout_botoes_acao);

        // Campos de entrada - Informações Pessoais
        editDataNascimento = findViewById(R.id.edit_data_nascimento);
        editTelefone = findViewById(R.id.edit_telefone);
        editEndereco = findViewById(R.id.edit_endereco);
        editCidade = findViewById(R.id.edit_cidade);
        editEstado = findViewById(R.id.edit_estado);
        editCep = findViewById(R.id.edit_cep);

        // Campos de entrada - Informações de Saúde
        editPeso = findViewById(R.id.edit_peso);
        editAltura = findViewById(R.id.edit_altura);
        editTipoSanguineo = findViewById(R.id.edit_tipo_sanguineo);
        editAlergias = findViewById(R.id.edit_alergias);
        editCondicoes = findViewById(R.id.edit_condicoes);
        editMedicamentosContinuos = findViewById(R.id.edit_medicamentos_continuos);

        // Campos de entrada - Contato de Emergência
        editContatoNome = findViewById(R.id.edit_contato_nome);
        editContatoTelefone = findViewById(R.id.edit_contato_telefone);
        editContatoParentesco = findViewById(R.id.edit_contato_parentesco);

        // Campos de entrada - Informações Médicas
        editNomeMedico = findViewById(R.id.edit_nome_medico);
        editTelefoneMedico = findViewById(R.id.edit_telefone_medico);
        editPlanoSaude = findViewById(R.id.edit_plano_saude);
        editNumeroCarteirinha = findViewById(R.id.edit_numero_carteirinha);
        editObservacoes = findViewById(R.id.edit_observacoes);

        // Botões
        buttonSalvar = findViewById(R.id.button_salvar);
        buttonCancelar = findViewById(R.id.button_cancelar);

        // Configura dropdown de tipos sanguíneos
        String[] tiposSanguineos = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                tiposSanguineos
        );
        editTipoSanguineo.setAdapter(adapter);
    }

    /**
     * Configura os listeners.
     */
    private void setupListeners() {
        // Data de nascimento com DatePicker
        editDataNascimento.setOnClickListener(v -> showDatePicker());

        // Botão salvar
        buttonSalvar.setOnClickListener(v -> salvarPerfil());

        // Botão cancelar
        buttonCancelar.setOnClickListener(v -> finish());

        // Calcula IMC quando peso ou altura mudam
        TextWatcher imcWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calcularEExibirIMC();
            }
        };
        editPeso.addTextChangedListener(imcWatcher);
        editAltura.addTextChangedListener(imcWatcher);
    }

    /**
     * Carrega o perfil do usuário.
     */
    private void loadUserProfile() {
        usuarioAtual = sessionManager.getUsuario();
        
        if (usuarioAtual == null) {
            // Não está logado - é o usuário comum acessando
            modoEdicao = false; // Usuário comum não pode editar
            
            // Exibe informações genéricas
            textNomeUsuario.setText("Usuário");
            textEmailUsuario.setText("Perfil do Usuário");
        } else {
            // Está logado
            textNomeUsuario.setText("Perfil do Usuário");
            textEmailUsuario.setText("Gerenciado por: " + usuarioAtual.nome);
            
            // Apenas admin pode editar
            modoEdicao = sessionManager.isUserAdmin();
        }
        
        configurarModoEdicao(modoEdicao);

        // Carrega o perfil único do banco
        perfilRepository.getPerfil().observe(this, perfil -> {
            if (perfil != null) {
                perfilAtual = perfil;
                preencherCampos(perfil);
            } else {
                // Perfil não existe - campos ficam vazios
                perfilAtual = null;
            }
        });
    }

    /**
     * Configura o modo de edição ou visualização.
     */
    private void configurarModoEdicao(boolean edicao) {
        // Habilita/desabilita campos
        setFieldsEnabled(edicao);

        // Mostra/esconde botões de ação
        layoutBotoesAcao.setVisibility(edicao ? View.VISIBLE : View.GONE);

        // Atualiza título
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(edicao ? "Editar Perfil" : "Visualizar Perfil");
        }
    }

    /**
     * Habilita ou desabilita todos os campos de entrada.
     */
    private void setFieldsEnabled(boolean enabled) {
        editDataNascimento.setEnabled(enabled);
        editTelefone.setEnabled(enabled);
        editEndereco.setEnabled(enabled);
        editCidade.setEnabled(enabled);
        editEstado.setEnabled(enabled);
        editCep.setEnabled(enabled);
        editPeso.setEnabled(enabled);
        editAltura.setEnabled(enabled);
        editTipoSanguineo.setEnabled(enabled);
        editAlergias.setEnabled(enabled);
        editCondicoes.setEnabled(enabled);
        editMedicamentosContinuos.setEnabled(enabled);
        editContatoNome.setEnabled(enabled);
        editContatoTelefone.setEnabled(enabled);
        editContatoParentesco.setEnabled(enabled);
        editNomeMedico.setEnabled(enabled);
        editTelefoneMedico.setEnabled(enabled);
        editPlanoSaude.setEnabled(enabled);
        editNumeroCarteirinha.setEnabled(enabled);
        editObservacoes.setEnabled(enabled);
        
        // Data de nascimento não é clicável se desabilitado
        editDataNascimento.setFocusable(enabled);
        editDataNascimento.setClickable(enabled);
    }

    /**
     * Preenche os campos com os dados do perfil.
     */
    private void preencherCampos(PerfilUsuario perfil) {
        editDataNascimento.setText(perfil.dataNascimento != null ? perfil.dataNascimento : "");
        editTelefone.setText(perfil.telefone != null ? perfil.telefone : "");
        editEndereco.setText(perfil.endereco != null ? perfil.endereco : "");
        editCidade.setText(perfil.cidade != null ? perfil.cidade : "");
        editEstado.setText(perfil.estado != null ? perfil.estado : "");
        editCep.setText(perfil.cep != null ? perfil.cep : "");
        editPeso.setText(perfil.peso != null ? String.valueOf(perfil.peso) : "");
        editAltura.setText(perfil.altura != null ? String.valueOf(perfil.altura) : "");
        editTipoSanguineo.setText(perfil.tipoSanguineo != null ? perfil.tipoSanguineo : "", false);
        editAlergias.setText(perfil.alergias != null ? perfil.alergias : "");
        editCondicoes.setText(perfil.condicoesMedicas != null ? perfil.condicoesMedicas : "");
        editMedicamentosContinuos.setText(perfil.medicamentosContinuos != null ? perfil.medicamentosContinuos : "");
        editContatoNome.setText(perfil.contatoEmergenciaNome != null ? perfil.contatoEmergenciaNome : "");
        editContatoTelefone.setText(perfil.contatoEmergenciaTelefone != null ? perfil.contatoEmergenciaTelefone : "");
        editContatoParentesco.setText(perfil.contatoEmergenciaParentesco != null ? perfil.contatoEmergenciaParentesco : "");
        editNomeMedico.setText(perfil.nomeMedico != null ? perfil.nomeMedico : "");
        editTelefoneMedico.setText(perfil.telefoneMedico != null ? perfil.telefoneMedico : "");
        editPlanoSaude.setText(perfil.planoSaude != null ? perfil.planoSaude : "");
        editNumeroCarteirinha.setText(perfil.numeroCarteirinha != null ? perfil.numeroCarteirinha : "");
        editObservacoes.setText(perfil.observacoes != null ? perfil.observacoes : "");

        // Calcula e exibe IMC
        calcularEExibirIMC();
    }

    /**
     * Mostra o seletor de data.
     */
    private void showDatePicker() {
        if (!modoEdicao) return;

        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String data = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            dayOfMonth, month + 1, year);
                    editDataNascimento.setText(data);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    /**
     * Calcula e exibe o IMC.
     */
    private void calcularEExibirIMC() {
        String pesoStr = editPeso.getText() != null ? editPeso.getText().toString() : "";
        String alturaStr = editAltura.getText() != null ? editAltura.getText().toString() : "";

        if (!pesoStr.isEmpty() && !alturaStr.isEmpty()) {
            try {
                double peso = Double.parseDouble(pesoStr);
                double altura = Double.parseDouble(alturaStr);
                
                if (peso > 0 && altura > 0) {
                    double alturaMetros = altura / 100.0;
                    double imc = peso / (alturaMetros * alturaMetros);
                    
                    DecimalFormat df = new DecimalFormat("#.##");
                    textImcValor.setText(df.format(imc));
                    textImcClassificacao.setText(classificarIMC(imc));
                    layoutImcInfo.setVisibility(View.VISIBLE);
                    return;
                }
            } catch (NumberFormatException e) {
                // Ignora erro de formatação
            }
        }
        
        layoutImcInfo.setVisibility(View.GONE);
    }

    /**
     * Classifica o IMC.
     */
    private String classificarIMC(double imc) {
        if (imc < 18.5) return "Abaixo do peso";
        else if (imc < 25) return "Peso normal";
        else if (imc < 30) return "Sobrepeso";
        else if (imc < 35) return "Obesidade grau I";
        else if (imc < 40) return "Obesidade grau II";
        else return "Obesidade grau III";
    }

    /**
     * Salva o perfil do usuário.
     */
    private void salvarPerfil() {
        if (!modoEdicao) {
            Toast.makeText(this, "Você não tem permissão para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Coleta dados dos campos
        if (perfilAtual == null) {
            // Cria novo perfil
            perfilAtual = new PerfilUsuario();
        }

        perfilAtual.dataNascimento = editDataNascimento.getText() != null ? 
                editDataNascimento.getText().toString() : null;
        perfilAtual.telefone = editTelefone.getText() != null ? 
                editTelefone.getText().toString() : null;
        perfilAtual.endereco = editEndereco.getText() != null ? 
                editEndereco.getText().toString() : null;
        perfilAtual.cidade = editCidade.getText() != null ? 
                editCidade.getText().toString() : null;
        perfilAtual.estado = editEstado.getText() != null ? 
                editEstado.getText().toString() : null;
        perfilAtual.cep = editCep.getText() != null ? 
                editCep.getText().toString() : null;
        
        // Peso e altura
        String pesoStr = editPeso.getText() != null ? editPeso.getText().toString() : "";
        perfilAtual.peso = !pesoStr.isEmpty() ? Double.parseDouble(pesoStr) : null;
        
        String alturaStr = editAltura.getText() != null ? editAltura.getText().toString() : "";
        perfilAtual.altura = !alturaStr.isEmpty() ? Double.parseDouble(alturaStr) : null;
        
        perfilAtual.tipoSanguineo = editTipoSanguineo.getText() != null ? 
                editTipoSanguineo.getText().toString() : null;
        perfilAtual.alergias = editAlergias.getText() != null ? 
                editAlergias.getText().toString() : null;
        perfilAtual.condicoesMedicas = editCondicoes.getText() != null ? 
                editCondicoes.getText().toString() : null;
        perfilAtual.medicamentosContinuos = editMedicamentosContinuos.getText() != null ? 
                editMedicamentosContinuos.getText().toString() : null;
        perfilAtual.contatoEmergenciaNome = editContatoNome.getText() != null ? 
                editContatoNome.getText().toString() : null;
        perfilAtual.contatoEmergenciaTelefone = editContatoTelefone.getText() != null ? 
                editContatoTelefone.getText().toString() : null;
        perfilAtual.contatoEmergenciaParentesco = editContatoParentesco.getText() != null ? 
                editContatoParentesco.getText().toString() : null;
        perfilAtual.nomeMedico = editNomeMedico.getText() != null ? 
                editNomeMedico.getText().toString() : null;
        perfilAtual.telefoneMedico = editTelefoneMedico.getText() != null ? 
                editTelefoneMedico.getText().toString() : null;
        perfilAtual.planoSaude = editPlanoSaude.getText() != null ? 
                editPlanoSaude.getText().toString() : null;
        perfilAtual.numeroCarteirinha = editNumeroCarteirinha.getText() != null ? 
                editNumeroCarteirinha.getText().toString() : null;
        perfilAtual.observacoes = editObservacoes.getText() != null ? 
                editObservacoes.getText().toString() : null;

        // Salva no banco
        perfilRepository.salvarPerfil(perfilAtual, new PerfilUsuarioRepository.OnOperationCompleteListener() {
            @Override
            public void onComplete(long id) {
                runOnUiThread(() -> {
                    Toast.makeText(PerfilActivity.this, "Perfil salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(PerfilActivity.this, "Erro ao salvar perfil: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
