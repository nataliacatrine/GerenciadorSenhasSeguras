package service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Classe responsável pela definição, verificação, armazenamento segura e derivação da senha mestra.
 * Utiliza PBKDF2 com HmacSHA256 para gerar hashes e derivar a chave AES a partir da senha do usuário.
 */
public class SenhaMestra {

    private static final String ARQUIVO_HASH = "senha_mestra.hash";
    private static final int ITERACOES = 65536;
    private static final int TAMANHO_CHAVE = 256;

    /**
     * Verifica se já existe uma senha mestra registrada no sistema.
     *
     * @return true se o arquivo de hash da senha mestra existir; false caso contrário.
     */
    public static boolean existeSenhaMestra() {
        return new File(ARQUIVO_HASH).exists();
    }

    /**
     * Define e armazena uma nova senha mestra.
     * Gera um salt aleatório e salva o hash da senha no formato Base64(salt):Base64(hash).
     *
     * @param senha A nova senha mestra a ser definida.
     * @throws IOException se ocorrer erro ao gravar o arquivo.
     */
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

    /**
     * Verifica se a senha fornecida corresponde à senha mestra armazenada.
     *
     * @param senha A senha digitada pelo usuário.
     * @return true se a senha for válida; false caso contrário.
     * @throws IOException se ocorrer erro ao ler o arquivo de hash.
     */
    public static boolean verificarSenha(String senha) throws IOException {
        if (senha == null) {
            return false;
        }
        String[] partes = new String(Files.readAllBytes(new File(ARQUIVO_HASH).toPath()), StandardCharsets.UTF_8).split(":");
        byte[] salt = Base64.getDecoder().decode(partes[0]);
        String hashArmazenado = partes[1];

        String hashDigitado = hashSenha(senha, salt);
        return hashArmazenado.equals(hashDigitado);
    }

    /**
     * Gera o hash da senha utilizando PBKDF2 com o salt fornecido.
     *
     * @param senha A senha a ser hasheada.
     * @param salt  O salt usado na derivação do hash.
     * @return Hash da senha em Base64.
     */
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

    /**
     * Gera um salt aleatório de 16 bytes para ser usado na derivação da senha.
     *
     * @return Array de bytes representando o salt.
     */
    private static byte[] gerarSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Deriva a chave mestra AES a partir da senha fornecida,
     * utilizando o salt armazenado no arquivo da senha mestra.
     *
     * @param senha A senha fornecida pelo usuário.
     * @return Chave AES derivada a partir da senha.
     * @throws IOException se ocorrer erro ao acessar o arquivo de hash.
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
     * Remove o arquivo que contém o hash da senha mestra, efetivamente apagando a senha armazenada.
     *
     * @return true se o arquivo foi apagado com sucesso; false caso contrário.
     */
    public static boolean apagarSenhaMestra() {
        File arquivo = new File(ARQUIVO_HASH);
        if (arquivo.exists()) {
            return arquivo.delete();
        }
        return false;
    }
}
