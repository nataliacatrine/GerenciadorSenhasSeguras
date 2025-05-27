package app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import util.Autenticador2FA;
import com.warrenstrange.googleauth.GoogleAuthenticator;

public class Autenticador2FATest {

    @Test
    public void testeGerarChaveSecreta() {
        String chave = Autenticador2FA.gerarChaveSecreta();
        assertNotNull(chave, "Chave secreta não deve ser nula");
        assertFalse(chave.isEmpty(), "Chave secreta não deve ser vazia");
    }

    @Test
    public void testeValidarTokenValido() {
        String chave = Autenticador2FA.gerarChaveSecreta();
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        int tokenValido = gAuth.getTotpPassword(chave);

        boolean resultado = Autenticador2FA.validarToken(chave, tokenValido);

        assertTrue(resultado, "O token válido deve ser aceito");
    }

    @Test
    public void testeValidarTokenInvalido() {
        String chave = Autenticador2FA.gerarChaveSecreta();
        int tokenInvalido = 123456; // token fixo que provavelmente é inválido

        boolean resultado = Autenticador2FA.validarToken(chave, tokenInvalido);

        assertFalse(resultado, "Um token inválido não deve ser aceito");
    }
}
