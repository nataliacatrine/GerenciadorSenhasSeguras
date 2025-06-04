package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

/**
 * Classe utilitária responsável por verificar se uma senha foi comprometida
 * utilizando a API pública do projeto Have I Been Pwned.
 * A verificação é feita por meio do protocolo de k-anonimato,
 * enviando apenas os primeiros 5 caracteres do hash SHA-1 da senha.
 */
public class VerificadorVazamentoSenha {

    /**
     * Verifica se a senha informada já apareceu em vazamentos de dados conhecidos.
     *
     * @param senha A senha em texto plano que será verificada.
     * @return true se a senha foi comprometida; false caso contrário.
     * @throws Exception se ocorrer erro de rede ou de hash.
     */
    public static boolean senhaVazada(String senha) throws Exception {
        String sha1 = sha1Hex(senha).toUpperCase();
        String prefixo = sha1.substring(0, 5);
        String sufixo = sha1.substring(5);

        URL url = new URL("https://api.pwnedpasswords.com/range/" + prefixo);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "GerenciadorSenhasSegurasApp");
        con.setConnectTimeout(3000);
        con.setReadTimeout(3000);

        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("API respondeu com código: " + responseCode);
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String linha;
            while ((linha = in.readLine()) != null) {
                if (linha.startsWith(sufixo)) {
                    return true; // Senha encontrada na base de dados vazados
                }
            }
        }
        return false; // Senha não encontrada
    }

    /**
     * Calcula o hash SHA-1 da senha fornecida.
     *
     * @param input A senha em texto plano.
     * @return Representação hexadecimal do hash SHA-1.
     * @throws Exception se ocorrer erro na geração do hash.
     */
    private static String sha1Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}