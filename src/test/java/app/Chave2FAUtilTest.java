package service;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class Chave2FAUtilTest {

    private static final String ARQUIVO_CHAVE = "chave_2fa.txt";

    @BeforeEach
    void limparArquivo() {
        File file = new File(ARQUIVO_CHAVE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testSalvarECarregarChave() throws IOException {
        String chaveOriginal = "MINHA_CHAVE_SECRETA_123";

        Chave2FAUtil.salvarChave(chaveOriginal);

        String chaveCarregada = Chave2FAUtil.carregarChave();

        assertNotNull(chaveCarregada);
        assertEquals(chaveOriginal, chaveCarregada);
    }

    @Test
    void testCarregarChaveArquivoNaoExiste() throws IOException {
        // Arquivo já deve estar limpo no BeforeEach
        String chave = Chave2FAUtil.carregarChave();
        assertNull(chave, "Deve retornar null quando arquivo não existir");
    }
}
