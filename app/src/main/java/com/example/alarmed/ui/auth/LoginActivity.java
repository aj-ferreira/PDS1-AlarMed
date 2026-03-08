package com.example.alarmed.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.alarmed.R;
import com.example.alarmed.data.db.entity.Usuario;
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
    private TextView textEsqueceuSenha;
    
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
        textEsqueceuSenha = findViewById(R.id.text_esqueceu_senha);
        
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

        textEsqueceuSenha.setOnClickListener(v -> mostrarDialogEsqueciSenha());

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

    // Dialog 1: pede o e-mail cadastrado.
    private void mostrarDialogEsqueciSenha() {
        EditText editEmailRecuperar = new EditText(this);
        editEmailRecuperar.setHint("Seu e-mail cadastrado");
        editEmailRecuperar.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editEmailRecuperar.setTextSize(16f);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding / 2, padding, 0);
        container.addView(editEmailRecuperar);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Recuperar senha")
                .setMessage("Informe o e-mail cadastrado na sua conta.")
                .setView(container)
                .setPositiveButton("Continuar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String email = editEmailRecuperar.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                editEmailRecuperar.setError("Campo obrigatório");
                return;
            }
            dialog.dismiss();
            verificarEmailERedefinir(email);
        });
    }

    /**
     * Busca o usuário pelo e-mail no banco.
     * Se encontrado, abre o Dialog 2. Se não, avisa o usuário.
     */
    private void verificarEmailERedefinir(String email) {
        usuarioRepository.buscarPorEmail(email, new UsuarioRepository.OnBuscarPorEmailListener() {
            @Override
            public void onResult(Usuario usuario) {
                runOnUiThread(() -> {
                    if (usuario == null) {
                        Toast.makeText(LoginActivity.this,
                                "E-mail não encontrado. Verifique e tente novamente.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        mostrarDialogNovaSenha(usuario.id);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this,
                                "Erro ao buscar usuário. Tente novamente.",
                                Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    // Dialog 2: pede a nova senha e confirmação.
    private void mostrarDialogNovaSenha(int usuarioId) {
        EditText editNovaSenha = new EditText(this);
        editNovaSenha.setHint("Nova senha");
        editNovaSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editNovaSenha.setTextSize(16f);

        EditText editConfirmarSenha = new EditText(this);
        editConfirmarSenha.setHint("Confirme a nova senha");
        editConfirmarSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editConfirmarSenha.setTextSize(16f);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        int margin = (int) (12 * getResources().getDisplayMetrics().density);
        container.setPadding(padding, padding / 2, padding, 0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = margin;
        container.addView(editNovaSenha, params);
        container.addView(editConfirmarSenha);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Nova senha")
                .setMessage("Crie uma nova senha para sua conta.")
                .setView(container)
                .setPositiveButton("Salvar", null)
                .setNegativeButton("Cancelar", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String novaSenha = editNovaSenha.getText().toString();
            String confirmar = editConfirmarSenha.getText().toString();

            if (TextUtils.isEmpty(novaSenha)) {
                editNovaSenha.setError("Campo obrigatório");
                return;
            }
            if (novaSenha.length() < 4) {
                editNovaSenha.setError("A senha deve ter ao menos 4 caracteres");
                return;
            }
            if (!novaSenha.equals(confirmar)) {
                editConfirmarSenha.setError("As senhas não coincidem");
                return;
            }

            dialog.dismiss();
            usuarioRepository.atualizarSenha(usuarioId, novaSenha, new UsuarioRepository.OnOperationCompleteListener() {
                @Override
                public void onComplete() {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this,
                                    "Senha alterada com sucesso! Faça login com a nova senha.",
                                    Toast.LENGTH_LONG).show()
                    );
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this,
                                    "Erro ao alterar a senha. Tente novamente.",
                                    Toast.LENGTH_SHORT).show()
                    );
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
