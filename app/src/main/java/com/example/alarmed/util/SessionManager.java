package com.example.alarmed.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.alarmed.data.db.entity.Usuario;
//TALVEZ NAO VAI USAR
/**
 * Gerenciador de sessão do usuário.
 * Utiliza SharedPreferences para armazenar informações da sessão atual.
 */
public class SessionManager {
    private static final String PREF_NAME = "AlarMedSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PERFIL = "userPerfil";
    
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    /**
     * Construtor do SessionManager.
     * @param context Contexto da aplicação.
     */
    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Cria uma sessão de usuário após login bem-sucedido.
     * @param usuario Usuário que fez login.
     */
    public void criarSessao(Usuario usuario) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, usuario.id);
        editor.putString(KEY_USER_NAME, usuario.nome);
        editor.putString(KEY_USER_EMAIL, usuario.email);
        editor.putString(KEY_USER_PERFIL, usuario.tipoPerfil);
        editor.apply();
    }

    /**
     * Verifica se existe uma sessão ativa.
     * @return true se o usuário está logado, false caso contrário.
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Obtém o ID do usuário logado.
     * @return ID do usuário ou -1 se não houver sessão.
     */
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    /**
     * Obtém o nome do usuário logado.
     * @return Nome do usuário ou null se não houver sessão.
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    /**
     * Obtém o email do usuário logado.
     * @return Email do usuário ou null se não houver sessão.
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    /**
     * Obtém o perfil do usuário logado.
     * @return Tipo de perfil ("ADMIN" ou "USUARIO") ou null se não houver sessão.
     */
    public String getUserPerfil() {
        return sharedPreferences.getString(KEY_USER_PERFIL, null);
    }

    /**
     * Verifica se o usuário logado é administrador.
     * @return true se o usuário é admin, false caso contrário.
     */
    public boolean isAdmin() {
        String perfil = getUserPerfil();
        return "ADMIN".equals(perfil);
    }

    /**
     * Verifica se o usuário logado é um usuário comum.
     * @return true se o usuário é comum, false caso contrário.
     */
    public boolean isUsuario() {
        String perfil = getUserPerfil();
        return "USUARIO".equals(perfil);
    }

    /**
     * Atualiza o nome do usuário na sessão.
     * @param nome Novo nome do usuário.
     */
    public void updateUserName(String nome) {
        editor.putString(KEY_USER_NAME, nome);
        editor.apply();
    }

    /**
     * Atualiza o email do usuário na sessão.
     * @param email Novo email do usuário.
     */
    public void updateUserEmail(String email) {
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    /**
     * Encerra a sessão do usuário (logout).
     * Remove todas as informações da sessão.
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    /**
     * Limpa completamente todas as preferências.
     * Use com cuidado - remove TODOS os dados salvos.
     */
    public void clearAll() {
        editor.clear();
        editor.apply();
    }
}
