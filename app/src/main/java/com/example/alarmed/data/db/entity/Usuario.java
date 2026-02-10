package com.example.alarmed.data.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entidade que representa um usuário no sistema.
 * Suporta diferentes perfis de acesso (ADMIN e USUARIO).
 */
@Entity(tableName = "usuario",
        indices = {@Index(value = {"email"}, unique = true)})
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "nome")
    public String nome;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "senha")
    public String senha;

    @ColumnInfo(name = "tipo_perfil")
    public String tipoPerfil; // "ADMIN" ou "USUARIO"

    @ColumnInfo(name = "data_criacao")
    public String dataCriacao;

    @ColumnInfo(name = "ativo")
    public boolean ativo;

    /**
     * Construtor padrão necessário para o Room.
     */
    public Usuario() {
        this.dataCriacao = String.valueOf(System.currentTimeMillis());
        this.ativo = true;
    }

    /**
     * Construtor completo.
     */
    public Usuario(String nome, String email, String senha, String tipoPerfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipoPerfil = tipoPerfil;
        this.dataCriacao = String.valueOf(System.currentTimeMillis());
        this.ativo = true;
    }

    /**
     * Verifica se o usuário tem perfil de administrador.
     */
    public boolean isAdmin() {
        return "ADMIN".equals(tipoPerfil);
    }

    /**
     * Verifica se o usuário tem perfil de usuário comum.
     */
    public boolean isUsuario() {
        return "USUARIO".equals(tipoPerfil);
    }
}
