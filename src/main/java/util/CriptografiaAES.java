package util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CriptografiaAES {

    private static final String ALGORITMO = "AES/ECB/PKCS5Padding";

    // Gera uma chave AES aleatória de 128 bits (16 bytes) como SecretKey
    public static SecretKey gerarChaveAES() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // 128 bits
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar chave AES", e);
        }
    }

    // Converte SecretKey para Base64 (String)
    public static String chaveParaBase64(SecretKey chave) {
        return Base64.getEncoder().encodeToString(chave.getEncoded());
    }

    // Converte Base64 para SecretKey
    public static SecretKey base64ParaChave(String chaveBase64) {
        byte[] decoded = Base64.getDecoder().decode(chaveBase64);
        return new SecretKeySpec(decoded, "AES");
    }

    // Encripta texto com chave AES em Base64
    public static String encrypt(String texto, String chaveBase64) {
        try {
            byte[] chaveBytes = Base64.getDecoder().decode(chaveBase64);
            SecretKeySpec key = new SecretKeySpec(chaveBytes, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] textoEncriptado = cipher.doFinal(texto.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(textoEncriptado);
        } catch (Exception e) {
            throw new RuntimeException("Erro na criptografia AES", e);
        }
    }

    // Decripta texto com chave AES em Base64
    public static String decrypt(String textoEncriptado, String chaveBase64) {
        try {
            byte[] chaveBytes = Base64.getDecoder().decode(chaveBase64);
            SecretKeySpec key = new SecretKeySpec(chaveBytes, "AES");
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] textoDecodificado = Base64.getDecoder().decode(textoEncriptado);
            byte[] textoDecriptado = cipher.doFinal(textoDecodificado);
            return new String(textoDecriptado, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Erro na descriptografia AES", e);
        }
    }
}