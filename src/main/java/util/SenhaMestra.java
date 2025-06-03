package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Base64;

public class SenhaMestra {

    private static final String ARQUIVO_HASH = "senha_mestra.hash";
    private static final int ITERACOES = 65536;
    private static final int TAMANHO_CHAVE = 256;

    public static boolean existeSenhaMestra() {
        return new File(ARQUIVO_HASH).exists();
    }

    public static void definirNovaSenha(String senha) throws IOException {
        if (senha == null) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }
        byte[] salt = gerarSalt();
        String hash = hashSenha(senha, salt);

        try (FileWriter writer = new FileWriter(ARQUIVO_HASH)) {
            writer.write(Base64.getEncoder().encodeToString(salt) + ":" + hash);
        }
    }

    public static boolean verificarSenha(String senha) throws IOException {
        if (senha == null) {
            // Se senha for null, já retorna false para evitar NullPointerException
            return false;
        }
        String[] partes = new String(Files.readAllBytes(new File(ARQUIVO_HASH).toPath()), StandardCharsets.UTF_8).split(":");
        byte[] salt = Base64.getDecoder().decode(partes[0]);
        String hashArmazenado = partes[1];

        String hashDigitado = hashSenha(senha, salt);
        return hashArmazenado.equals(hashDigitado);
    }

    private static String hashSenha(String senha, byte[] salt) {
        if (senha == null) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, ITERACOES, TAMANHO_CHAVE);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }

    private static byte[] gerarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Deriva a chave mestra AES a partir da senha fornecida,
     * utilizando o salt armazenado no arquivo hash.
     */
    public static SecretKey obterChaveMestra(String senha) throws IOException {
        if (senha == null) {
            throw new IllegalArgumentException("Senha não pode ser nula");
        }
        String[] partes = new String(Files.readAllBytes(new File(ARQUIVO_HASH).toPath()), StandardCharsets.UTF_8).split(":");
        byte[] salt = Base64.getDecoder().decode(partes[0]);

        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, ITERACOES, TAMANHO_CHAVE);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] chave = skf.generateSecret(spec).getEncoded();
            return new SecretKeySpec(chave, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao derivar chave mestra", e);
        }
    }

    /**
     * Apaga de forma segura a senha mestra armazenada removendo o arquivo hash do disco.
     * Retorna true se o arquivo foi apagado com sucesso, false caso contrário.
     */
    public static boolean apagarSenhaMestra() {
        File arquivo = new File(ARQUIVO_HASH);
        if (arquivo.exists()) {
            return arquivo.delete();
        }
        return false;
    }
}
