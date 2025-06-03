package app;

import org.junit.jupiter.api.*;
import util.SenhaMestra;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SenhaMestraTest {

    private final String caminhoHash = "senha_mestra.hash";

    @BeforeEach
    void limparHash() {
        File arquivo = new File(caminhoHash);
        if (arquivo.exists()) {
            arquivo.delete();
        }
    }

    @Test
    void testDefinirEVerificarSenha() throws IOException {
        assertFalse(SenhaMestra.existeSenhaMestra());

        String senha = "minhaSenhaSegura";
        SenhaMestra.definirNovaSenha(senha);
        assertTrue(SenhaMestra.existeSenhaMestra());

        assertTrue(SenhaMestra.verificarSenha(senha));
        assertFalse(SenhaMestra.verificarSenha("senhaErrada"));
    }
}

