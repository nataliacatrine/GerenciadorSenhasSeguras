package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class VerificadorVazamentoSenha {

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
                    return true; // Senha vazada
                }
            }
        }
        return false; // Senha não vazada
    }

    private static String sha1Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] bytes = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}