package util;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;

public class CriptografiaChaves {
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding"; // Usar modo + padding para consistência
    private static final int KEY_SIZE = 256;

    // Gera chave AES de 256 bits (usado para chave mestra)
    public static SecretKey gerarChaveAES() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    // Deriva chave mestra a partir da senha e salt
    public static SecretKey derivarChaveMestra(char[] senhaMestra, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(senhaMestra, salt, 65536, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    // Criptografa chave AES com chave mestra e retorna Base64
    public static String criptografarChaveAES(SecretKey chaveAES, SecretKey chaveMestra) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, chaveMestra);
        byte[] criptografada = cipher.doFinal(chaveAES.getEncoded());
        return Base64.getEncoder().encodeToString(criptografada);
    }

    // Descriptografa chave AES criptografada em Base64 usando chave mestra
    public static SecretKey descriptografarChaveAES(String chaveCriptografadaBase64, SecretKey chaveMestra) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, chaveMestra);
        byte[] decoded = Base64.getDecoder().decode(chaveCriptografadaBase64);
        byte[] chaveAESBytes = cipher.doFinal(decoded);
        return new SecretKeySpec(chaveAESBytes, "AES");
    }
}