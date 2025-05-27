package util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GeradorSenhaTest {

    @Test
    void testGerarSenha() {
        String senha = GeradorSenha.gerarSenha(12);
        Assertions.assertNotNull(senha);
        Assertions.assertEquals(12, senha.length());
    }
}