package com.example.alarmed.data.repos;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.alarmed.data.db.AppDatabase;
import com.example.alarmed.data.db.daos.UsuarioDao;
import com.example.alarmed.data.db.entity.Usuario;
import com.example.alarmed.util.PasswordUtil;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositório para gerenciar operações relacionadas a Usuários.
 * Abstrai o acesso ao DAO e gerencia operações assíncronas.
 */
public class UsuarioRepository {
    private static final String TAG = "UsuarioRepository";
    
    private final UsuarioDao usuarioDao;
    private final ExecutorService executor;

    /**
     * Construtor do repositório.
     * @param context Contexto da aplicação.
     */
    public UsuarioRepository(Context context) {
        Log.d(TAG, "Inicializando UsuarioRepository...");
        AppDatabase db = AppDatabase.getDatabase(context);
        this.usuarioDao = db.usuarioDao();
        this.executor = Executors.newSingleThreadExecutor();
        Log.d(TAG, "UsuarioRepository inicializado com sucesso");
    }

    // --- Operações de Leitura (Síncronas com LiveData) ---

    /**
     * Obtém um usuário por ID.
     * @param id ID do usuário.
     * @return LiveData com o usuário.
     */
    public LiveData<Usuario> getUsuarioById(int id) {
        return usuarioDao.getUsuarioById(id);
    }

    /**
     * Obtém todos os usuários ativos.
     * @return LiveData com a lista de usuários.
     */
    public LiveData<List<Usuario>> getAllUsuarios() {
        return usuarioDao.getAllUsuarios();
    }

    /**
     * Obtém todos os administradores.
     * @return LiveData com a lista de administradores.
     */
    public LiveData<List<Usuario>> getAdministradores() {
        return usuarioDao.getAdministradores();
    }

    /**
     * Obtém todos os usuários comuns.
     * @return LiveData com a lista de usuários comuns.
     */
    public LiveData<List<Usuario>> getUsuariosComuns() {
        return usuarioDao.getUsuariosComuns();
    }

    // --- Operações de Escrita (Assíncronas) ---

    /**
     * Insere um novo usuário no banco de dados.
     * @param usuario Usuário a ser inserido.
     * @param callback Callback com o ID do usuário inserido.
     */
    public void insert(Usuario usuario, OnUsuarioInsertListener callback) {
        executor.execute(() -> {
            try {
                // Criptografa a senha antes de inserir
                if (usuario.senha != null && !PasswordUtil.isHashed(usuario.senha)) {
                    usuario.senha = PasswordUtil.hashPassword(usuario.senha);
                }
                
                long id = usuarioDao.insert(usuario);
                Log.d(TAG, "Usuário inserido com ID: " + id);
                if (callback != null) {
                    callback.onInsertComplete(id);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao inserir usuário", e);
                if (callback != null) {
                    callback.onInsertError(e);
                }
            }
        });
    }

    /**
     * Atualiza um usuário existente.
     * @param usuario Usuário a ser atualizado.
     * @param callback Callback de conclusão.
     */
    public void update(Usuario usuario, OnOperationCompleteListener callback) {
        executor.execute(() -> {
            try {
                // Criptografa a senha antes de atualizar se não estiver criptografada
                if (usuario.senha != null && !PasswordUtil.isHashed(usuario.senha)) {
                    usuario.senha = PasswordUtil.hashPassword(usuario.senha);
                }
                
                usuarioDao.update(usuario);
                Log.d(TAG, "Usuário atualizado: " + usuario.id);
                if (callback != null) {
                    callback.onComplete();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao atualizar usuário", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * Deleta um usuário.
     * @param usuario Usuário a ser deletado.
     * @param callback Callback de conclusão.
     */
    public void delete(Usuario usuario, OnOperationCompleteListener callback) {
        executor.execute(() -> {
            try {
                usuarioDao.delete(usuario);
                Log.d(TAG, "Usuário deletado: " + usuario.id);
                if (callback != null) {
                    callback.onComplete();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao deletar usuário", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * Autentica um usuário.
     * @param email Email do usuário.
     * @param senha Senha do usuário.
     * @param callback Callback com o resultado da autenticação.
     */
    public void autenticar(String email, String senha, OnAuthListener callback) {
        executor.execute(() -> {
            try {
                // Busca o usuário pelo email
                Usuario usuario = usuarioDao.getUsuarioByEmail(email);
                
                // Verifica se o usuário existe e se a senha está correta
                if (usuario != null && usuario.senha != null) {
                    boolean senhaCorreta = PasswordUtil.verifyPassword(senha, usuario.senha);
                    if (!senhaCorreta) {
                        usuario = null; // Senha incorreta
                    }
                }
                
                Log.d(TAG, "Autenticação: " + (usuario != null ? "sucesso" : "falha"));
                if (callback != null) {
                    callback.onAuthResult(usuario);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao autenticar usuário", e);
                if (callback != null) {
                    callback.onAuthError(e);
                }
            }
        });
    }

    /**
     * Garante que o usuário comum (ID 2) exista no banco de dados.
     * Se não existir, cria um usuário comum padrão.
     * @param callback Callback com o ID do usuário comum.
     */
    public void garantirUsuarioComum(OnUsuarioComumListener callback) {
        executor.execute(() -> {
            try {
                // Verifica se o usuário comum já existe
                Usuario usuarioComum = usuarioDao.getUsuarioByIdSync(2);
                
                if (usuarioComum == null) {
                    // Cria um usuário comum padrão
                    Usuario novoUsuario = new Usuario();
                    novoUsuario.id = 2;
                    novoUsuario.nome = "Usuário Comum";
                    novoUsuario.email = "usuario@alarmed.com";
                    novoUsuario.senha = PasswordUtil.hashPassword("usuario123");
                    novoUsuario.tipoPerfil = "USUARIO";
                    novoUsuario.ativo = true;
                    
                    usuarioDao.insert(novoUsuario);
                    Log.d(TAG, "Usuário comum criado com ID: 2");
                }
                
                if (callback != null) {
                    callback.onUsuarioComumReady(2);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao garantir usuário comum", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * Verifica se um email já existe no banco de dados.
     * @param email Email a ser verificado.
     * @param callback Callback com o resultado.
     */
    public void emailExiste(String email, OnEmailExistsListener callback) {
        executor.execute(() -> {
            try {
                int count = usuarioDao.emailExiste(email);
                boolean existe = count > 0;
                Log.d(TAG, "Email " + email + " existe: " + existe);
                if (callback != null) {
                    callback.onResult(existe);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao verificar email", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * Desativa um usuário.
     * @param id ID do usuário.
     * @param callback Callback de conclusão.
     */
    public void desativarUsuario(int id, OnOperationCompleteListener callback) {
        executor.execute(() -> {
            try {
                usuarioDao.desativarUsuario(id);
                Log.d(TAG, "Usuário desativado: " + id);
                if (callback != null) {
                    callback.onComplete();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao desativar usuário", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    // Busca um usuário pelo email.
    public void buscarPorEmail(String email, OnBuscarPorEmailListener callback) {
        executor.execute(() -> {
            try {
                Usuario usuario = usuarioDao.getUsuarioByEmail(email);
                Log.d(TAG, "Busca por email " + email + ": " + (usuario != null ? "encontrado" : "não encontrado"));
                if (callback != null) {
                    callback.onResult(usuario);
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao buscar usuário por email", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    // Atualiza a senha de um usuário.
    public void atualizarSenha(int id, String novaSenha, OnOperationCompleteListener callback) {
        executor.execute(() -> {
            try {
                // Criptografa a nova senha antes de atualizar
                String senhaCriptografada = PasswordUtil.hashPassword(novaSenha);
                usuarioDao.atualizarSenha(id, senhaCriptografada);
                Log.d(TAG, "Senha atualizada para usuário: " + id);
                if (callback != null) {
                    callback.onComplete();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao atualizar senha", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    // --- Interfaces de Callback ---

    public interface OnUsuarioInsertListener {
        void onInsertComplete(long id);
        void onInsertError(Exception e);
    }

    public interface OnOperationCompleteListener {
        void onComplete();
        void onError(Exception e);
    }

    public interface OnAuthListener {
        void onAuthResult(Usuario usuario);
        void onAuthError(Exception e);
    }

    public interface OnEmailExistsListener {
        void onResult(boolean existe);
        void onError(Exception e);
    }

    public interface OnBuscarPorEmailListener {
        void onResult(Usuario usuario);
        void onError(Exception e);
    }

    public interface OnUsuarioComumListener {
        void onUsuarioComumReady(int usuarioId);
        void onError(Exception e);
    }
}
