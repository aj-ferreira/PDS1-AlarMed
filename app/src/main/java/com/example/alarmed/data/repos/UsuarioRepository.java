package com.example.alarmed.data.repos;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.alarmed.data.db.AppDatabase;
import com.example.alarmed.data.db.daos.UsuarioDao;
import com.example.alarmed.data.db.entity.Usuario;

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
                Usuario usuario = usuarioDao.autenticar(email, senha);
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

    /**
     * Atualiza a senha de um usuário.
     * @param id ID do usuário.
     * @param novaSenha Nova senha.
     * @param callback Callback de conclusão.
     */
    public void atualizarSenha(int id, String novaSenha, OnOperationCompleteListener callback) {
        executor.execute(() -> {
            try {
                usuarioDao.atualizarSenha(id, novaSenha);
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
}
