package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Chave2FAUtil {
    private static final String ARQUIVO_CHAVE = "chave_2fa.txt";

    // Salva a chave secreta 2FA em arquivo
    public static void salvarChave(String chave) throws IOException {
        Files.write(Paths.get(ARQUIVO_CHAVE), chave.getBytes());
    }

    // Carrega a chave secreta 2FA do arquivo; retorna null se arquivo não existir
    public static String carregarChave() throws IOException {
        File file = new File(ARQUIVO_CHAVE);
        if (!file.exists()) {
            return null;
        }
        return new String(Files.readAllBytes(Paths.get(ARQUIVO_CHAVE)));
    }
}