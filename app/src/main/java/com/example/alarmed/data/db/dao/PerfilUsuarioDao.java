package com.example.alarmed.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmed.data.db.entity.PerfilUsuario;

/**
 * DAO para operações com perfil de usuário.
 * Segue o princípio da Inversão de Dependência (DIP).
 */
@Dao
public interface PerfilUsuarioDao {

    /**
     * Insere um novo perfil de usuário.
     * Se já existir um perfil para o usuário, substitui.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PerfilUsuario perfil);

    /**
     * Atualiza um perfil de usuário existente.
     */
    @Update
    void update(PerfilUsuario perfil);

    /**
     * Deleta um perfil de usuário.
     */
    @Delete
    void delete(PerfilUsuario perfil);

    /**
     * Busca o perfil único do usuário comum.
     * Retorna LiveData para observação de mudanças.
     * Como existe apenas um perfil, retorna o primeiro registro.
     */
    @Query("SELECT * FROM perfil_usuario LIMIT 1")
    LiveData<PerfilUsuario> getPerfil();

    /**
     * Busca o perfil único (sem LiveData).
     * Útil para operações síncronas.
     */
    @Query("SELECT * FROM perfil_usuario LIMIT 1")
    PerfilUsuario getPerfilSync();

    /**
     * Verifica se já existe um perfil cadastrado.
     */
    @Query("SELECT COUNT(*) FROM perfil_usuario")
    int countPerfis();
}
