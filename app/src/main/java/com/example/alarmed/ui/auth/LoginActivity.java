package com.example.alarmed.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmed.R;
import com.example.alarmed.data.repos.UsuarioRepository;
import com.example.alarmed.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Activity de Login.
 * Segue o Princípio da Responsabilidade Única - responsável apenas pela UI de login.
 */
public class LoginActivity extends AppCompatActivity {
    
    private TextInputEditText editEmail;
    private TextInputEditText editSenha;
    private Button btnEntrar;
    private TextView textIrParaCadastro;
    
    private UsuarioRepository usuarioRepository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Inicializa componentes
        initializeComponents();
        setupListeners();
    }

    /**
     * Inicializa os componentes da view.
     * Princípio da Responsabilidade Única.
     */
    private void initializeComponents() {
        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.edit_senha);
        btnEntrar = findViewById(R.id.btn_entrar);
        textIrParaCadastro = findViewById(R.id.text_ir_para_cadastro);
        
        usuarioRepository = new UsuarioRepository(this);
        sessionManager = new SessionManager(this);
        
        // Configura ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Login");
        }
    }

    /**
     * Configura os listeners dos componentes.
     * Princípio da Responsabilidade Única.
     */
    private void setupListeners() {
        btnEntrar.setOnClickListener(v -> realizarLogin());
        
        textIrParaCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Realiza o login do usuário.
     * Princípio da Responsabilidade Única - delega autenticação ao repository.
     */
    private void realizarLogin() {
        String email = editEmail.getText() != null ? editEmail.getText().toString().trim() : "";
        String senha = editSenha.getText() != null ? editSenha.getText().toString() : "";
        
        // Validações
        if (TextUtils.isEmpty(email)) {
            editEmail.setError("Campo obrigatório");
            editEmail.requestFocus();
            return;
        }
        
        if (TextUtils.isEmpty(senha)) {
            editSenha.setError("Campo obrigatório");
            editSenha.requestFocus();
            return;
        }
        
        // Desabilita botão durante autenticação
        btnEntrar.setEnabled(false);
        btnEntrar.setText("Entrando...");
        
        // Autentica usuário
        usuarioRepository.autenticar(email, senha, new UsuarioRepository.OnAuthListener() {
            @Override
            public void onAuthResult(com.example.alarmed.data.db.entity.Usuario usuario) {
                runOnUiThread(() -> {
                    if (usuario != null) {
                        // Login bem-sucedido
                        sessionManager.criarSessao(usuario);
                        Toast.makeText(LoginActivity.this, 
                                "Bem-vindo, " + usuario.nome + "!", 
                                Toast.LENGTH_SHORT).show();
                        
                        // Volta para a activity anterior
                        finish();
                    } else {
                        // Credenciais inválidas
                        Toast.makeText(LoginActivity.this, 
                                "Email ou senha inválidos", 
                                Toast.LENGTH_LONG).show();
                        btnEntrar.setEnabled(true);
                        btnEntrar.setText("Entrar");
                    }
                });
            }

            @Override
            public void onAuthError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, 
                            "Erro ao fazer login: " + e.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    btnEntrar.setEnabled(true);
                    btnEntrar.setText("Entrar");
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
