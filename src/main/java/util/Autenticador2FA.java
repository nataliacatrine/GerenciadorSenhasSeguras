package util;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

public class Autenticador2FA {
    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public static String gerarChaveSecreta() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public static boolean validarToken(String chaveSecreta, int token) {
        return gAuth.authorize(chaveSecreta, token);
    }
}