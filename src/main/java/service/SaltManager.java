package service;

import java.io.*;
import java.security.SecureRandom;

/**
 * Classe utilitária para gerenciamento do salt usado na derivação da chave mestra.
 * O salt é salvo em arquivo binário e reutilizado entre execuções para garantir consistência na derivação.
 */
public class SaltManager {

    private static final String SALT_PATH = "dados/salt.bin";
    private static final int TAMANHO_SALT = 16;

    /**
     * Gera um novo salt aleatório com tamanho definido.
     *
     * @return um array de bytes representando o novo salt.
     */
    public static byte[] gerarNovoSalt() {
        byte[] salt = new byte[TAMANHO_SALT];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    /**
     * Carrega um salt previamente salvo do arquivo, ou gera um novo caso não exista.
     *
     * @return um array de bytes representando o salt.
     * @throws IOException se ocorrer falha ao ler ou gravar o arquivo de salt.
     */
    public static byte[] carregarOuGerarSalt() throws IOException {
        File file = new File(SALT_PATH);

        if (!file.exists()) {
            byte[] salt = gerarNovoSalt();
            salvarSalt(salt);
            return salt;
        }

        return carregarSalt();
    }

    /**
     * Salva o salt fornecido em um arquivo binário.
     *
     * @param salt array de bytes do salt a ser salvo.
     * @throws IOException se ocorrer falha ao gravar o arquivo.
     */
    private static void salvarSalt(byte[] salt) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(SALT_PATH)) {
            fos.write(salt);
        }
    }

    /**
     * Carrega o salt salvo anteriormente a partir do arquivo.
     *
     * @return array de bytes representando o salt carregado.
     * @throws IOException se o arquivo estiver corrompido, incompleto ou não puder ser lido.
     */
    private static byte[] carregarSalt() throws IOException {
        try (FileInputStream fis = new FileInputStream(SALT_PATH)) {
            byte[] salt = new byte[TAMANHO_SALT];
            if (fis.read(salt) != TAMANHO_SALT) {
                throw new IOException("Salt corrompido ou incompleto.");
            }
            return salt;
        }
    }
}
