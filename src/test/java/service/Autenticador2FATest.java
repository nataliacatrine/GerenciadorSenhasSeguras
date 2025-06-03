package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Autenticador2FATest {

    @Test
    void testGerarEValidarToken() {
        // Gera chave secreta
        String chaveSecreta = Autenticador2FA.gerarChaveSecreta();
        assertNotNull(chaveSecreta);

        // Gera token atual pela classe Autenticador2FA
        int tokenAtual = Autenticador2FA.gerarTokenAtual(chaveSecreta);

        // Valida token gerado
        boolean valido = Autenticador2FA.validarToken(chaveSecreta, tokenAtual);
        assertTrue(valido);

        // Testa token inválido (ex: token -1)
        boolean invalido = Autenticador2FA.validarToken(chaveSecreta, -1);
        assertFalse(invalido);
    }
}

