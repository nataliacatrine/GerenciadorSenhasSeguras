package util;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class HashSenha {

    public static String gerarHash(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public static boolean verificarSenha(String senha, String hash) {
        return BCrypt.checkpw(senha, hash);
    }
}