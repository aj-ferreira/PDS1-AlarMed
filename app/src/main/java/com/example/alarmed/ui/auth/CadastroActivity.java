package com.example.alarmed.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmed.R;
import com.example.alarmed.data.db.entity.Usuario;
import com.example.alarmed.data.repos.UsuarioRepository;
import com.example.alarmed.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Activity de Cadastro de Usuários.
 * Segue o Princípio da Responsabilidade Única - responsável apenas pela UI de cadastro.
 */
public class CadastroActivity extends AppCompatActivity {
    
    private TextInputEditText editNome;
    private TextInputEditText editEmail;
    private TextInputEditText editSenha;
    private TextInputEditText editConfirmarSenha;
    private LinearLayout layoutTipoPerfil;
    private RadioGroup radioGroupPerfil;
    private Button btnCadastrar;
    private TextView textIrParaLogin;
    
    private UsuarioRepository usuarioRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        
        initializeComponents();
        setupListeners();
        checkAdminPermissions();
    }

    private void initializeComponents() {
        editNome = findViewById(R.id.edit_nome);
        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.edit_senha);
        editConfirmarSenha = findViewById(R.id.edit_confirmar_senha);
        layoutTipoPerfil = findViewById(R.id.layout_tipo_perfil);
        radioGroupPerfil = findViewById(R.id.radio_group_perfil);
        btnCadastrar = findViewById(R.id.btn_cadastrar);
        textIrParaLogin = findViewById(R.id.text_ir_para_login);
        
        usuarioRepository = new UsuarioRepository(this);
        sessionManager = new SessionManager(this);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Criar Conta");
        }
    }

    private void setupListeners() {
        btnCadastrar.setOnClickListener(v -> realizarCadastro());
        
        textIrParaLogin.setOnClickListener(v -> {
            Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Verifica se o usuário logado é admin para mostrar opção de perfil.
     */
    private void checkAdminPermissions() {
        if (sessionManager.isLoggedIn() && sessionManager.isAdmin()) {
            layoutTipoPerfil.setVisibility(LinearLayout.VISIBLE);
        }
    }

    private void realizarCadastro() {
        String nome = editNome.getText() != null ? editNome.getText().toString().trim() : "";
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String senha = editSenha.getText() != null ? editSenha.getText().toString() : "";
        String confirmarSenha = editConfirmarSenha.getText() != null ? 
                editConfirmarSenha.getText().toString() : "";
        
        // Validações
        if (!validarCampos(nome, email, senha, confirmarSenha)) {
            return;
        }
        
        // Define o tipo de perfil
        String tipoPerfil = "USUARIO";
        if (layoutTipoPerfil.getVisibility() == LinearLayout.VISIBLE) {
            int selectedId = radioGroupPerfil.getCheckedRadioButtonId();
            if (selectedId == R.id.radio_admin) {
                tipoPerfil = "ADMIN";
            }
        }
        
        // Desabilita botão
        btnCadastrar.setEnabled(false);
        btnCadastrar.setText("Cadastrando...");
        
        // Verifica se email já existe
        final String finalTipoPerfil = tipoPerfil;
        usuarioRepository.emailExiste(email, new UsuarioRepository.OnEmailExistsListener() {
            @Override
            public void onResult(boolean existe) {
                runOnUiThread(() -> {
                    if (existe) {
                        Toast.makeText(CadastroActivity.this, 
                                "Este email já está cadastrado", 
                                Toast.LENGTH_LONG).show();
                        btnCadastrar.setEnabled(true);
                        btnCadastrar.setText("Cadastrar");
                    } else {
                        // Cria novo usuário
                        Usuario novoUsuario = new Usuario(nome, email, senha, finalTipoPerfil);
                        inserirUsuario(novoUsuario);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(CadastroActivity.this, 
                            "Erro ao verificar email: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    btnCadastrar.setEnabled(true);
                    btnCadastrar.setText("Cadastrar");
                });
            }
        });
    }

    private void inserirUsuario(Usuario usuario) {
        usuarioRepository.insert(usuario, new UsuarioRepository.OnUsuarioInsertListener() {
            @Override
            public void onInsertComplete(long id) {
                runOnUiThread(() -> {
                    Toast.makeText(CadastroActivity.this, 
                            "Cadastro realizado com sucesso!", 
                            Toast.LENGTH_SHORT).show();
                    
                    // Se não estiver logado, criar sessão automaticamente
                    if (!sessionManager.isLoggedIn()) {
                        usuario.id = (int) id;
                        sessionManager.criarSessao(usuario);
                    }
                    
                    finish();
                });
            }

            @Override
            public void onInsertError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(CadastroActivity.this, 
                            "Erro ao cadastrar: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    btnCadastrar.setEnabled(true);
                    btnCadastrar.setText("Cadastrar");
                });
            }
        });
    }

    private boolean validarCampos(String nome, String email, String senha, String confirmarSenha) {
        if (TextUtils.isEmpty(nome)) {
            editNome.setError("Campo obrigatório");
            editNome.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Campo obrigatório");
            editEmail.requestFocus();
            return false;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Email inválido");
            editEmail.requestFocus();
            return false;
        }
        
        if (TextUtils.isEmpty(senha)) {
            editSenha.setError("Campo obrigatório");
            editSenha.requestFocus();
            return false;
        }
        
        if (senha.length() < 6) {
            editSenha.setError("A senha deve ter no mínimo 6 caracteres");
            editSenha.requestFocus();
            return false;
        }
        
        if (!senha.equals(confirmarSenha)) {
            editConfirmarSenha.setError("As senhas não coincidem");
            editConfirmarSenha.requestFocus();
            return false;
        }
        
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
