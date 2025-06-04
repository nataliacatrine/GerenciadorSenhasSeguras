package service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utilitário para persistência da chave secreta 2FA (autenticação em duas etapas).
 * <p>
 * A chave é salva e lida de um arquivo local chamado {@code chave_2fa.txt}.
 */
public class Chave2FAUtil {

    /** Nome do arquivo onde a chave secreta 2FA é armazenada */
    private static final String ARQUIVO_CHAVE = "chave_2fa.txt";

    /**
     * Salva a chave secreta 2FA em um arquivo local.
     *
     * @param chave Chave secreta a ser armazenada
     * @throws IOException Se ocorrer erro ao gravar o arquivo
     */
    public static void salvarChave(String chave) throws IOException {
        Files.write(Paths.get(ARQUIVO_CHAVE), chave.getBytes());
    }

    /**
     * Carrega a chave secreta 2FA a partir do arquivo local.
     *
     * @return A chave secreta armazenada, ou {@code null} se o arquivo não existir
     * @throws IOException Se ocorrer erro ao ler o arquivo
     */
    public static String carregarChave() throws IOException {
        File file = new File(ARQUIVO_CHAVE);
        if (!file.exists()) {
            return null;
        }
        return new String(Files.readAllBytes(Paths.get(ARQUIVO_CHAVE)));
    }
}
