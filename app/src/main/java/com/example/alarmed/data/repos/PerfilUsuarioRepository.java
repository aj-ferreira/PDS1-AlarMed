package com.example.alarmed.data.repos;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.alarmed.data.db.AppDatabase;
import com.example.alarmed.data.db.dao.PerfilUsuarioDao;
import com.example.alarmed.data.db.entity.PerfilUsuario;

import java.util.List;

/**
 * Repository para gerenciar operações com perfil de usuário.
 * Como existe apenas um perfil (do usuário comum), este repository
 * gerencia um único registro no banco de dados.
 * Segue os princípios SOLID:
 * - SRP: Responsável apenas por operações de perfil
 * - DIP: Depende da abstração (DAO) e não da implementação
 */
public class PerfilUsuarioRepository {

    private PerfilUsuarioDao perfilUsuarioDao;

    public PerfilUsuarioRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        perfilUsuarioDao = db.perfilUsuarioDao();
    }

    /**
     * Interface para callback de operações assíncronas.
     */
    public interface OnOperationCompleteListener {
        void onComplete(long id);
        void onError(Exception e);
    }

    /**
     * Insere ou atualiza o perfil do usuário comum.
     */
    public void salvarPerfil(PerfilUsuario perfil, OnOperationCompleteListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Atualiza o timestamp
                perfil.dataAtualizacao = String.valueOf(System.currentTimeMillis());
                
                long id = perfilUsuarioDao.insert(perfil);
                if (listener != null) {
                    listener.onComplete(id);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e);
                }
            }
        });
    }

    /**
     * Atualiza o perfil existente.
     */
    public void atualizarPerfil(PerfilUsuario perfil) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            perfil.dataAtualizacao = String.valueOf(System.currentTimeMillis());
            perfilUsuarioDao.update(perfil);
        });
    }

    /**
     * Busca o perfil único do usuário comum.
     */
    public LiveData<PerfilUsuario> getPerfil() {
        return perfilUsuarioDao.getPerfil();
    }

    /**
     * Busca o perfil de forma síncrona.
     * Deve ser executado em background thread.
     */
    public PerfilUsuario getPerfilSync() {
        return perfilUsuarioDao.getPerfilSync();
    }

    /**
     * Deleta o perfil.
     */
    public void deletarPerfil(PerfilUsuario perfil) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            perfilUsuarioDao.delete(perfil);
        });
    }

    /**
     * Verifica se já existe um perfil cadastrado.
     * Retorna o resultado via callback.
     */
    public void verificarSeTemPerfil(OnVerificacaoListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                int count = perfilUsuarioDao.countPerfis();
                boolean temPerfil = count > 0;
                if (listener != null) {
                    listener.onResult(temPerfil);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e);
                }
            }
        });
    }

    /**
     * Interface para callback de verificação.
     */
    public interface OnVerificacaoListener {
        void onResult(boolean temPerfil);
        void onError(Exception e);
    }

    /**
     * Cria um perfil vazio.
     */
    public void criarPerfilVazio(OnOperationCompleteListener listener) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                PerfilUsuario perfil = new PerfilUsuario();
                long id = perfilUsuarioDao.insert(perfil);
                if (listener != null) {
                    listener.onComplete(id);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e);
                }
            }
        });
    }
}
