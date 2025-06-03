package util;

import java.io.*;
import java.security.SecureRandom;

public class SaltManager {
    private static final String SALT_PATH = "dados/salt.bin";
    private static final int TAMANHO_SALT = 16;

    // Gera um novo salt aleatório
    public static byte[] gerarNovoSalt() {
        byte[] salt = new byte[TAMANHO_SALT];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // Carrega um salt existente ou gera e salva um novo
    public static byte[] carregarOuGerarSalt() throws IOException {
        File file = new File(SALT_PATH);

        if (!file.exists()) {
            byte[] salt = gerarNovoSalt();
            salvarSalt(salt);
            return salt;
        }

        return carregarSalt();
    }

    // Salva o salt no arquivo
    private static void salvarSalt(byte[] salt) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(SALT_PATH)) {
            fos.write(salt);
        }
    }

    // Carrega o salt do arquivo
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