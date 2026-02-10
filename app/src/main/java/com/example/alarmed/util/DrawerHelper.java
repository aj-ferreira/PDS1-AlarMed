package com.example.alarmed.util;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.alarmed.R;
import com.example.alarmed.ui.auth.LoginActivity;
import com.example.alarmed.ui.perfil.PerfilActivity;
import com.google.android.material.navigation.NavigationView;

/**
 * Helper para gerenciar o Navigation Drawer.
 * Segue o Princípio da Responsabilidade Única (SRP) - responsável apenas pela gestão do drawer.
 * Segue o Princípio Aberto/Fechado (OCP) - pode ser estendido sem modificação.
 */
public class DrawerHelper implements NavigationView.OnNavigationItemSelectedListener {
    
    private final AppCompatActivity activity;
    private final DrawerLayout drawerLayout;
    private final NavigationView navigationView;
    private final ActionBarDrawerToggle toggle;
    private final SessionManager sessionManager;

    /**
     * Construtor do DrawerHelper.
     * Princípio da Inversão de Dependência (DIP) - depende de abstrações (Activity, DrawerLayout).
     * 
     * @param activity Activity que contém o drawer
     * @param drawerLayout DrawerLayout a ser gerenciado
     * @param navigationView NavigationView a ser gerenciado
     */
    public DrawerHelper(AppCompatActivity activity, DrawerLayout drawerLayout, NavigationView navigationView) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
        this.sessionManager = new SessionManager(activity);
        
        // Configura o toggle do drawer
        this.toggle = new ActionBarDrawerToggle(
                activity,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        // Define o listener para os itens do menu
        navigationView.setNavigationItemSelectedListener(this);
        
        // Atualiza o menu baseado no estado de login
        updateMenuForLoginState();
    }

    /**
     * Configura a ActionBar para exibir o ícone do menu hamburguer.
     */
    public void setupActionBar() {
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * Deve ser chamado em onOptionsItemSelected da Activity.
     * 
     * @param item Item do menu selecionado
     * @return true se o item foi tratado
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        // Trata o botão de voltar como fechar o drawer se estiver aberto
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Trata a navegação dos itens do menu.
     * Princípio da Segregação de Interface (ISP) - implementa apenas o necessário.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_login) {
            handleLoginNavigation();
        } else if (id == R.id.nav_perfil) {
            handlePerfilNavigation();
        }
        // Adicionar outros itens de navegação aqui
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Trata a navegação para login/logout.
     * Princípio da Responsabilidade Única - método específico para login.
     */
    private void handleLoginNavigation() {
        if (sessionManager.isLoggedIn()) {
            // Se já está logado, fazer logout
            sessionManager.logout();
            updateMenuForLoginState();
        } else {
            // Navegar para tela de login
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        }
    }

    /**
     * Trata a navegação para o perfil do usuário.
     * Princípio da Responsabilidade Única - método específico para perfil.
     * Usuário comum (não logado) pode visualizar, apenas admin pode editar.
     */
    private void handlePerfilNavigation() {
        // Permite acesso ao perfil mesmo sem login
        // O controle de edição é feito dentro da PerfilActivity
        Intent intent = new Intent(activity, PerfilActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Atualiza o menu baseado no estado de login do usuário.
     */
    private void updateMenuForLoginState() {
        MenuItem loginItem = navigationView.getMenu().findItem(R.id.nav_login);
        if (loginItem != null) {
            if (sessionManager.isLoggedIn()) {
                String perfil = sessionManager.isAdmin() ? " [ADMIN]" : " [Usuário]";
                loginItem.setTitle("Logout (" + sessionManager.getUserName() + perfil + ")");
                loginItem.setIcon(android.R.drawable.ic_lock_power_off);
            } else {
                loginItem.setTitle("Login");
                loginItem.setIcon(android.R.drawable.ic_lock_lock);
            }
        }
    }

    /**
     * Fecha o drawer se estiver aberto.
     * 
     * @return true se o drawer foi fechado
     */
    public boolean closeDrawerIfOpen() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    /**
     * Atualiza o estado do menu (deve ser chamado em onResume).
     */
    public void updateMenuState() {
        updateMenuForLoginState();
    }
}
