package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class SenhaMestra {

    private static final String ARQUIVO_HASH = "senha_mestra.hash";

    public static boolean existeSenhaMestra() {
        return new File(ARQUIVO_HASH).exists();
    }

    public static void definirNovaSenha(String senha) throws IOException {
        String hash = HashSenha.gerarHash(senha);
        try (FileWriter writer = new FileWriter(ARQUIVO_HASH)) {
            writer.write(hash);
        }
    }

    public static boolean verificarSenha(String senha) throws IOException {
        Path path = new File(ARQUIVO_HASH).toPath();
        byte[] bytes = Files.readAllBytes(path);
        String hash = new String(bytes, StandardCharsets.UTF_8);
        return HashSenha.verificarSenha(senha, hash);
    }
}