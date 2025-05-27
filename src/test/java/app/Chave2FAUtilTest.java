package util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

public class Chave2FAUtilTest {

    private static final String ARQUIVO_CHAVE = "chave_2fa.txt";

    @BeforeEach
    public void limparArquivoAntes() {
        File file = new File(ARQUIVO_CHAVE);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    public void limparArquivoDepois() {
        File file = new File(ARQUIVO_CHAVE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testSalvarECarregarChave() throws IOException {
        String chaveOriginal = "MINHA_CHAVE_SECRETA_2FA";

        // Salvar chave no arquivo
        Chave2FAUtil.salvarChave(chaveOriginal);

        // Carregar chave do arquivo
        String chaveCarregada = Chave2FAUtil.carregarChave();

        assertNotNull(chaveCarregada, "Chave carregada não deve ser nula");
        assertEquals(chaveOriginal, chaveCarregada, "Chave carregada deve ser igual à salva");
    }

    @Test
    public void testCarregarChaveQuandoArquivoNaoExiste() throws IOException {
        // Garante que arquivo não existe
        File file = new File(ARQUIVO_CHAVE);
        if (file.exists()) {
            file.delete();
        }

        String chaveCarregada = Chave2FAUtil.carregarChave();
        assertNull(chaveCarregada, "Deve retornar null quando arquivo não existir");
    }
}
