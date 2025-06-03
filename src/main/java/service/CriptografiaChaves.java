package service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class CriptografiaChaves {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256; // bits
    private static final int ITERATIONS = 65536;
    private static final int GCM_TAG_LENGTH = 128; // bits

    /**
     * Gera uma chave AES de 256 bits aleatória.
     */
    public static SecretKey gerarChaveAES() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    /**
     * Deriva uma chave mestra a partir da senha mestra do usuário e um salt.
     */
    public static SecretKey derivarChaveMestra(char[] senhaMestra, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(senhaMestra, salt, ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    /**
     * Criptografa uma chave AES usando a chave mestra.
     * Usa AES/GCM/NoPadding com IV gerado aleatoriamente.
     * Retorna resultado em Base64 com IV concatenado no início.
     */
    public static String criptografarChaveAES(SecretKey chaveAES, SecretKey chaveMestra) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = gerarIV(12); // 12 bytes para GCM
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, chaveMestra, spec);
        byte[] criptografada = cipher.doFinal(chaveAES.getEncoded());

        byte[] resultado = new byte[iv.length + criptografada.length];
        System.arraycopy(iv, 0, resultado, 0, iv.length);
        System.arraycopy(criptografada, 0, resultado, iv.length, criptografada.length);

        return Base64.getEncoder().encodeToString(resultado);
    }

    /**
     * Descriptografa uma chave AES criptografada em Base64 usando a chave mestra.
     * Espera que o IV esteja concatenado no início dos dados.
     */
    public static SecretKey descriptografarChaveAES(String chaveCriptografadaBase64, SecretKey chaveMestra) throws Exception {
        byte[] dados = Base64.getDecoder().decode(chaveCriptografadaBase64);
        byte[] iv = new byte[12];
        byte[] ciphertext = new byte[dados.length - iv.length];

        System.arraycopy(dados, 0, iv, 0, iv.length);
        System.arraycopy(dados, iv.length, ciphertext, 0, ciphertext.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, chaveMestra, spec);

        byte[] chaveAESBytes = cipher.doFinal(ciphertext);
        return new SecretKeySpec(chaveAESBytes, "AES");
    }

    /**
     * Criptografa um texto (como uma senha) com AES-GCM usando a chave AES individual.
     * O IV gerado aleatoriamente é concatenado no início do resultado codificado em Base64.
     */
    public static String criptografarTextoComAES(String texto, SecretKey chaveAES) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        byte[] iv = gerarIV(12);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, chaveAES, spec);
        byte[] cifrado = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));

        byte[] combinado = new byte[iv.length + cifrado.length];
        System.arraycopy(iv, 0, combinado, 0, iv.length);
        System.arraycopy(cifrado, 0, combinado, iv.length, cifrado.length);

        return Base64.getEncoder().encodeToString(combinado);
    }

    /**
     * Descriptografa um texto criptografado com AES-GCM e codificado em Base64.
     * Espera que o IV esteja concatenado no início dos dados.
     */
    public static String descriptografarTextoComAES(String base64, SecretKey chaveAES) throws Exception {
        byte[] combinado = Base64.getDecoder().decode(base64);
        byte[] iv = Arrays.copyOfRange(combinado, 0, 12);
        byte[] cifrado = Arrays.copyOfRange(combinado, 12, combinado.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, chaveAES, spec);
        byte[] decifrado = cipher.doFinal(cifrado);

        return new String(decifrado, StandardCharsets.UTF_8);
    }

    /**
     * Converte SecretKey para Base64 (String).
     */
    public static String chaveParaBase64(SecretKey chave) {
        return Base64.getEncoder().encodeToString(chave.getEncoded());
    }

    /**
     * Converte Base64 para SecretKey.
     */
    public static SecretKey base64ParaChave(String chaveBase64) {
        byte[] decoded = Base64.getDecoder().decode(chaveBase64);
        return new SecretKeySpec(decoded, "AES");
    }

    // Gera IV aleatório para AES-GCM
    private static byte[] gerarIV(int tamanho) {
        byte[] iv = new byte[tamanho];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }
}