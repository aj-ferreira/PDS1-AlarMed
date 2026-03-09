package com.example.alarmed.util;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Classe utilitária para criptografia e verificação de senhas.
 * Utiliza SHA-256 com salt para segurança.
 */
public class PasswordUtil {
    private static final String TAG = "PasswordUtil";
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Gera um hash criptografado da senha.
     * @param senha Senha em texto plano.
     * @return Senha criptografada (salt + hash).
     */
    public static String hashPassword(String senha) {
        try {
            // Gera um salt aleatório
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Cria o hash da senha com o salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(senha.getBytes());

            // Combina salt + hash e converte para Base64
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);

            String encoded = Base64.getEncoder().encodeToString(combined);
            Log.d(TAG, "Senha criptografada com sucesso");
            return encoded;

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Erro ao criptografar senha", e);
            throw new RuntimeException("Erro ao criptografar senha", e);
        }
    }

    /**
     * Verifica se uma senha corresponde ao hash armazenado.
     * @param senha Senha em texto plano.
     * @param hashedPassword Hash armazenado (salt + hash).
     * @return true se a senha corresponder, false caso contrário.
     */
    public static boolean verifyPassword(String senha, String hashedPassword) {
        try {
            // Decodifica o hash armazenado
            byte[] combined = Base64.getDecoder().decode(hashedPassword);

            // Extrai o salt
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);

            // Extrai o hash original
            byte[] originalHash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, originalHash, 0, originalHash.length);

            // Cria o hash da senha fornecida com o mesmo salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] testHash = md.digest(senha.getBytes());

            // Compara os hashes
            boolean matches = MessageDigest.isEqual(originalHash, testHash);
            Log.d(TAG, "Verificação de senha: " + (matches ? "sucesso" : "falha"));
            return matches;

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Erro ao verificar senha", e);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Erro ao decodificar senha", e);
            return false;
        }
    }

    /**
     * Verifica se uma senha já está criptografada.
     * @param senha Senha a ser verificada.
     * @return true se estiver criptografada, false caso contrário.
     */
    public static boolean isHashed(String senha) {
        if (senha == null || senha.isEmpty()) {
            return false;
        }
        
        try {
            byte[] decoded = Base64.getDecoder().decode(senha);
            // Um hash válido deve ter pelo menos o tamanho do salt + hash
            return decoded.length >= (SALT_LENGTH + 32); // SHA-256 produz 32 bytes
        } catch (IllegalArgumentException e) {
            // Não é Base64 válido, portanto não está criptografada
            return false;
        }
    }
}
