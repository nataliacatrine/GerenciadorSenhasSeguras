package util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class SenhaMestraTest {

    @AfterEach
    void cleanup() {
        // Limpa a senha mestra criada para não interferir em outros testes
        File file = new File("senhaMestra.dat"); // ajuste se seu arquivo for diferente
        if (file.exists()) file.delete();
    }

    @Test
    void testDefinirEVerificarSenhaMestra() throws Exception {
        String senha = "senhaTeste123!";
        SenhaMestra.definirNovaSenha(senha);

        Assertions.assertTrue(SenhaMestra.existeSenhaMestra());
        Assertions.assertTrue(SenhaMestra.verificarSenha(senha));
        Assertions.assertFalse(SenhaMestra.verificarSenha("senhaErrada"));
    }
}