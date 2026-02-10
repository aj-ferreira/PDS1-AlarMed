package com.example.alarmed.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Gerenciador de permissões baseado no perfil do usuário.
 * Centraliza as regras de acesso da aplicação seguindo o princípio SOLID.
 * 
 * Regras de acesso:
 * - ADMIN: Pode criar, editar e excluir medicamentos e horários
 * - USUARIO: Pode apenas visualizar medicamentos, histórico e marcar como tomado
 */
public class PermissionManager {
    
    private final SessionManager sessionManager;
    
    /**
     * Construtor do PermissionManager.
     * @param context Contexto da aplicação.
     */
    public PermissionManager(Context context) {
        this.sessionManager = new SessionManager(context);
    }
    
    /**
     * Verifica se o usuário atual pode criar medicamentos.
     * @return true se for ADMIN, false caso contrário.
     */
    public boolean canCreateMedicamentos() {
        return sessionManager.isAdmin();
    }
    
    /**
     * Verifica se o usuário atual pode editar medicamentos.
     * @return true se for ADMIN, false caso contrário.
     */
    public boolean canEditMedicamentos() {
        return sessionManager.isAdmin();
    }
    
    /**
     * Verifica se o usuário atual pode excluir medicamentos.
     * @return true se for ADMIN, false caso contrário.
     */
    public boolean canDeleteMedicamentos() {
        return sessionManager.isAdmin();
    }
    
    /**
     * Verifica se o usuário atual pode configurar horários.
     * @return true se for ADMIN, false caso contrário.
     */
    public boolean canManageHorarios() {
        return sessionManager.isAdmin();
    }
    
    /**
     * Verifica se o usuário atual pode visualizar medicamentos.
     * @return true para todos os usuários autenticados.
     */
    public boolean canViewMedicamentos() {
        return sessionManager.isLoggedIn();
    }
    
    /**
     * Verifica se o usuário atual pode marcar medicamentos como tomado.
     * @return true para todos os usuários autenticados.
     */
    public boolean canMarkAsTaken() {
        return sessionManager.isLoggedIn();
    }
    
    /**
     * Verifica se o usuário atual pode visualizar histórico.
     * @return true para todos os usuários autenticados.
     */
    public boolean canViewHistory() {
        return sessionManager.isLoggedIn();
    }
    
    /**
     * Verifica se o usuário atual pode gerar relatórios.
     * @return true para todos os usuários autenticados.
     */
    public boolean canGenerateReports() {
        return sessionManager.isLoggedIn();
    }
    
    /**
     * Verifica se o usuário tem permissão e exibe mensagem se não tiver.
     * Finaliza a activity se não tiver permissão.
     * 
     * @param activity Activity que está verificando a permissão.
     * @param permission Nome da permissão sendo verificada.
     * @param hasPermission Se o usuário tem a permissão.
     * @return true se tem permissão, false caso contrário.
     */
    public boolean checkPermissionOrFinish(Activity activity, String permission, boolean hasPermission) {
        if (!hasPermission) {
            Toast.makeText(activity, 
                "Acesso negado. Apenas administradores podem " + permission + ".", 
                Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        return true;
    }
    
    /**
     * Verifica se o usuário tem permissão e exibe mensagem se não tiver.
     * 
     * @param context Contexto da aplicação.
     * @param permission Nome da permissão sendo verificada.
     * @param hasPermission Se o usuário tem a permissão.
     * @return true se tem permissão, false caso contrário.
     */
    public boolean checkPermissionWithMessage(Context context, String permission, boolean hasPermission) {
        if (!hasPermission) {
            Toast.makeText(context, 
                "Acesso negado. Apenas administradores podem " + permission + ".", 
                Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    
    /**
     * Obtém o SessionManager subjacente.
     * @return SessionManager
     */
    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
