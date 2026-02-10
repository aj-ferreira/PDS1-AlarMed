package com.example.alarmed.data.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmed.data.db.entity.Usuario;

import java.util.List;

/**
 * DAO (Data Access Object) para a entidade Usuario.
 * Define as operações de acesso ao banco de dados para usuários.
 */
@Dao
public interface UsuarioDao {

    /**
     * Insere um novo usuário no banco de dados.
     * @param usuario O usuário a ser inserido.
     * @return O ID do usuário inserido.
     */
    @Insert
    long insert(Usuario usuario);

    /**
     * Atualiza um usuário existente.
     * @param usuario O usuário a ser atualizado.
     */
    @Update
    void update(Usuario usuario);

    /**
     * Deleta um usuário do banco de dados.
     * @param usuario O usuário a ser deletado.
     */
    @Delete
    void delete(Usuario usuario);

    /**
     * Busca um usuário por email e senha para autenticação.
     * @param email O email do usuário.
     * @param senha A senha do usuário.
     * @return O usuário encontrado ou null.
     */
    @Query("SELECT * FROM usuario WHERE email = :email AND senha = :senha AND ativo = 1 LIMIT 1")
    Usuario autenticar(String email, String senha);

    /**
     * Busca um usuário por ID.
     * @param id O ID do usuário.
     * @return LiveData com o usuário encontrado.
     */
    @Query("SELECT * FROM usuario WHERE id = :id")
    LiveData<Usuario> getUsuarioById(int id);

    /**
     * Busca um usuário por email.
     * @param email O email do usuário.
     * @return O usuário encontrado ou null.
     */
    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    Usuario getUsuarioByEmail(String email);

    /**
     * Lista todos os usuários ativos.
     * @return LiveData com a lista de usuários ativos.
     */
    @Query("SELECT * FROM usuario WHERE ativo = 1 ORDER BY nome ASC")
    LiveData<List<Usuario>> getAllUsuarios();

    /**
     * Lista todos os usuários administradores.
     * @return LiveData com a lista de administradores.
     */
    @Query("SELECT * FROM usuario WHERE tipo_perfil = 'ADMIN' AND ativo = 1 ORDER BY nome ASC")
    LiveData<List<Usuario>> getAdministradores();

    /**
     * Lista todos os usuários comuns.
     * @return LiveData com a lista de usuários comuns.
     */
    @Query("SELECT * FROM usuario WHERE tipo_perfil = 'USUARIO' AND ativo = 1 ORDER BY nome ASC")
    LiveData<List<Usuario>> getUsuariosComuns();

    /**
     * Busca um usuário por ID de forma síncrona.
     * @param id O ID do usuário.
     * @return O usuário encontrado ou null.
     */
    @Query("SELECT * FROM usuario WHERE id = :id")
    Usuario getUsuarioByIdSync(int id);

    /**
     * Verifica se existe pelo menos um administrador no sistema.
     * @return Número de administradores ativos.
     */
    @Query("SELECT COUNT(*) FROM usuario WHERE tipo_perfil = 'ADMIN' AND ativo = 1")
    int countAdministradores();

    /**
     * Verifica se um email já está cadastrado.
     * @param email O email a ser verificado.
     * @return Número de usuários com esse email.
     */
    @Query("SELECT COUNT(*) FROM usuario WHERE email = :email")
    int emailExiste(String email);

    /**
     * Desativa um usuário (soft delete).
     * @param id O ID do usuário a ser desativado.
     */
    @Query("UPDATE usuario SET ativo = 0 WHERE id = :id")
    void desativarUsuario(int id);

    /**
     * Reativa um usuário.
     * @param id O ID do usuário a ser reativado.
     */
    @Query("UPDATE usuario SET ativo = 1 WHERE id = :id")
    void reativarUsuario(int id);

    /**
     * Atualiza a senha de um usuário.
     * @param id O ID do usuário.
     * @param novaSenha A nova senha.
     */
    @Query("UPDATE usuario SET senha = :novaSenha WHERE id = :id")
    void atualizarSenha(int id, String novaSenha);
}
