package util;

import java.io.*;
import java.security.SecureRandom;

public class SaltManager {
    private static final String SALT_PATH = "dados/salt.bin";

    // Retorna um salt existente ou cria um novo se não existir
    public static byte[] carregarOuGerarSalt() throws IOException {
        File file = new File(SALT_PATH);

        if (!file.exists()) {
            // Cria um novo salt aleatório
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            // Salva em arquivo
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(salt);
            }

            return salt;
        }

        // Carrega o salt do arquivo
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] salt = new byte[16];
            if (fis.read(salt) != 16) {
                throw new IOException("Salt corrompido ou incompleto.");
            }
            return salt;
        }
    }
}