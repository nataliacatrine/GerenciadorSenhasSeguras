package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VerificadorVazamentoSenhaTest {

    @Test
    void testSenhaNaoVazada() throws Exception {
        String senhaUnica = "SenhaUnicaMuitoForte" + System.nanoTime();
        assertFalse(VerificadorVazamentoSenha.senhaVazada(senhaUnica));
    }

    @Test
    void testSenhaVazada() throws Exception {
        assertTrue(VerificadorVazamentoSenha.senhaVazada("123456"));
    }
}
