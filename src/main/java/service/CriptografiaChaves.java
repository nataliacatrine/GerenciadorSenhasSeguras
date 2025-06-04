package service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

/**
 * Classe utilitária para operações de criptografia e descriptografia usando AES-GCM.
 * Inclui métodos para criptografar e descriptografar chaves AES e textos,
 * além de derivação de chave mestra com PBKDF2.
 */
public class CriptografiaChaves {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256; // bits
    private static final int ITERATIONS = 65536;
    private static final int GCM_TAG_LENGTH = 128; // bits

    /**
     * Gera uma chave AES de 256 bits aleatória.
     *
     * @return Nova chave secreta AES
     * @throws NoSuchAlgorithmException Se o algoritmo AES não estiver disponível
     */
    public static SecretKey gerarChaveAES() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(KEY_SIZE);
        return keyGen.generateKey();
    }

    /**
     * Deriva uma chave mestra com base em uma senha e um salt, utilizando PBKDF2.
     *
     * @param senhaMestra Senha mestre em formato de array de caracteres
     * @param salt Salt aleatório
     * @return Chave derivada como {@code SecretKey}
     * @throws Exception Se ocorrer erro na derivação
     */
    public static SecretKey derivarChaveMestra(char[] senhaMestra, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(senhaMestra, salt, ITERATIONS, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    /**
     * Criptografa uma chave AES usando uma chave mestra.
     * O IV é gerado aleatoriamente e incluído no início do resultado codificado em Base64.
     *
     * @param chaveAES    Chave AES a ser criptografada
     * @param chaveMestra Chave mestra utilizada para criptografar
     * @return String em Base64 contendo IV + chave criptografada
     * @throws Exception Se ocorrer erro na criptografia
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
     * Descriptografa uma chave AES criptografada com AES-GCM em Base64.
     * O IV deve estar incluído no início da string codificada.
     *
     * @param chaveCriptografadaBase64 Chave criptografada codificada em Base64
     * @param chaveMestra              Chave mestra usada na descriptografia
     * @return Chave AES original
     * @throws Exception Se ocorrer erro na descriptografia
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
     * Criptografa um texto usando AES-GCM com uma chave AES.
     * O IV é gerado aleatoriamente e incluído no início do resultado codificado em Base64.
     *
     * @param texto    Texto em claro a ser criptografado
     * @param chaveAES Chave AES usada na criptografia
     * @return Texto criptografado em Base64
     * @throws Exception Se ocorrer erro na criptografia
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
     * Descriptografa um texto criptografado com AES-GCM codificado em Base64.
     * O IV deve estar incluído no início da string codificada.
     *
     * @param base64   Texto criptografado em Base64
     * @param chaveAES Chave AES usada para descriptografar
     * @return Texto original em claro
     * @throws Exception Se ocorrer erro na descriptografia
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
     * Converte uma chave secreta {@code SecretKey} para uma string Base64.
     *
     * @param chave Chave secreta
     * @return Representação em Base64
     */
    public static String chaveParaBase64(SecretKey chave) {
        return Base64.getEncoder().encodeToString(chave.getEncoded());
    }

    /**
     * Converte uma string Base64 de volta para {@code SecretKey}.
     *
     * @param chaveBase64 Representação Base64 de uma chave
     * @return Objeto {@code SecretKey}
     */
    public static SecretKey base64ParaChave(String chaveBase64) {
        byte[] decoded = Base64.getDecoder().decode(chaveBase64);
        return new SecretKeySpec(decoded, "AES");
    }

    /**
     * Gera um vetor de inicialização (IV) aleatório com o tamanho especificado.
     *
     * @param tamanho Tamanho em bytes do IV
     * @return Vetor de bytes IV
     */
    private static byte[] gerarIV(int tamanho) {
        byte[] iv = new byte[tamanho];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }
}
