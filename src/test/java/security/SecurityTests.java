package security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import util.SenhaMestra;

import java.io.File;
import java.io.IOException;

public class SecurityTests {

    private static final String ARQUIVO_HASH = "senha_mestra.hash";

    @BeforeEach
    public void setup() throws IOException {
        // Apaga o arquivo para começar limpo antes de cada teste
        SenhaMestra.apagarSenhaMestra();
    }

    @AfterEach
    public void cleanup() {
        // Limpa arquivo após cada teste para evitar interferência
        SenhaMestra.apagarSenhaMestra();
    }

    @Test
    public void testDefinirEVerificarSenhaCorreta() throws IOException {
        String senha = "senha123";
        SenhaMestra.definirNovaSenha(senha);
        assertTrue(SenhaMestra.existeSenhaMestra());
        assertTrue(SenhaMestra.verificarSenha(senha));
    }

    @Test
    public void testVerificarSenhaIncorreta() throws IOException {
        String senha = "senha123";
        SenhaMestra.definirNovaSenha(senha);
        assertFalse(SenhaMestra.verificarSenha("senhaErrada"));
    }

    @Test
    public void testVerificarSenhaNula() throws IOException {
        String senha = "senha123";
        SenhaMestra.definirNovaSenha(senha);
        assertFalse(SenhaMestra.verificarSenha(null));
    }

    @Test
    public void testDefinirSenhaNulaLancaException() {
        assertThrows(IllegalArgumentException.class, () -> {
            SenhaMestra.definirNovaSenha(null);
        });
    }

    @Test
    public void testObterChaveMestraComSenhaValida() throws IOException {
        String senha = "senha123";
        SenhaMestra.definirNovaSenha(senha);
        assertNotNull(SenhaMestra.obterChaveMestra(senha));
    }

    @Test
    public void testObterChaveMestraComSenhaNulaLancaException() throws IOException {
        String senha = "senha123";
        SenhaMestra.definirNovaSenha(senha);

        assertThrows(IllegalArgumentException.class, () -> {
            SenhaMestra.obterChaveMestra(null);
        });
    }

    @Test
    public void testApagarSenhaMestra() throws IOException {
        String senha = "senha123";
        SenhaMestra.definirNovaSenha(senha);
        assertTrue(SenhaMestra.existeSenhaMestra());

        assertTrue(SenhaMestra.apagarSenhaMestra());
        assertFalse(SenhaMestra.existeSenhaMestra());
    }

    @Test
    public void testApagarSenhaMestraSemArquivoRetornaFalse() {
        assertFalse(SenhaMestra.apagarSenhaMestra());
    }
}
