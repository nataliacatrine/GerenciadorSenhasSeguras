package service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

/**
 * Serviço responsável pela autenticação em duas etapas (2FA) utilizando TOTP (Time-based One-Time Password).
 * <p>
 * Usa a biblioteca {@code GoogleAuthenticator} para gerar e validar tokens com base em uma chave secreta.
 */
public class Autenticador2FA {

    /** Instância do autenticador baseada em TOTP */
    private static final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    /**
     * Gera uma nova chave secreta base32, que será utilizada para configurar o 2FA.
     *
     * @return Chave secreta gerada (string codificada em base32)
     */
    public static String gerarChaveSecreta() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    /**
     * Valida um token TOTP fornecido com base na chave secreta associada.
     *
     * @param chaveSecreta Chave secreta base32 vinculada ao usuário
     * @param token Token TOTP informado pelo usuário (gerado no app autenticador)
     * @return {@code true} se o token for válido; caso contrário, {@code false}
     */
    public static boolean validarToken(String chaveSecreta, int token) {
        return gAuth.authorize(chaveSecreta, token);
    }

    /**
     * Gera o token TOTP atual com base em uma chave secreta.
     * <p>
     * Útil para testes automatizados ou integração.
     *
     * @param chaveSecreta Chave secreta base32
     * @return Token TOTP gerado para o momento atual
     */
    public static int gerarTokenAtual(String chaveSecreta) {
        return gAuth.getTotpPassword(chaveSecreta);
    }
}
