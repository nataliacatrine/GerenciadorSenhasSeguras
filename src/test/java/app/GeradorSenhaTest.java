package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GeradorSenhaTest {

    @Test
    public void testGerarSenhaComTamanhoCorreto() {
        String senha = GeradorSenha.gerarSenha(16);
        assertNotNull(senha);
        assertEquals(16, senha.length());
    }

    @Test
    public void testGerarSenhasUnicas() {
        String s1 = GeradorSenha.gerarSenha(12);
        String s2 = GeradorSenha.gerarSenha(12);
        assertNotEquals(s1, s2);
    }
}