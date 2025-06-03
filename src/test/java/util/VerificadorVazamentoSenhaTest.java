package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VerificadorVazamentoSenhaTest {

    @Test
    void testSenhaNaoVazada() {
        String senhaUnica = "SenhaUnicaMuitoForte" + System.nanoTime();
        assertFalse(VerificadorVazamentoSenha.senhaVazada(senhaUnica));
    }

    @Test
    void testSenhaVazada() {
        assertTrue(VerificadorVazamentoSenha.senhaVazada("123456"));
    }
}