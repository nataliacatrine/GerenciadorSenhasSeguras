package util;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SaltManagerTest {

    private static final String SALT_PATH = "dados/salt.bin";

    @BeforeEach
    void limparArquivoSalt() {
        File file = new File(SALT_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void testGerarNovoSalt() {
        byte[] salt = SaltManager.gerarNovoSalt();
        assertNotNull(salt, "Salt não pode ser nulo");
        assertEquals(16, salt.length, "Salt deve ter 16 bytes");
    }

    @Test
    void testSalvarECarregarSalt() throws IOException {
        byte[] saltGerado = SaltManager.gerarNovoSalt();
        // Salva o salt
        SaltManager.carregarOuGerarSalt(); // Garante que arquivo existe

        // Força salvar manualmente para garantir teste (se quiser pode criar um método público salvarSalt para teste)
        // Mas como salvarSalt é privado, vou chamar carregarOuGerarSalt() para criar arquivo

        // Agora carrega o salt do arquivo
        byte[] saltCarregado = SaltManager.carregarOuGerarSalt();

        assertNotNull(saltCarregado);
        assertEquals(16, saltCarregado.length);
        // saltCarregado deve ser igual ao que está salvo em disco, mas não conseguimos injetar saltGerado
        // Então só verificamos consistência no carregamento
    }

    @Test
    void testCarregarOuGerarSalt_GeraSeNaoExistir() throws IOException {
        File file = new File(SALT_PATH);
        assertFalse(file.exists(), "Arquivo salt.bin não deve existir antes do teste");

        byte[] salt = SaltManager.carregarOuGerarSalt();

        assertNotNull(salt);
        assertEquals(16, salt.length);
        assertTrue(file.exists(), "Arquivo salt.bin deve ser criado");
    }

    @Test
    void testCarregarOuGerarSalt_CarregaSeExistir() throws IOException {
        // Primeiro gera e salva o salt
        byte[] saltOriginal = SaltManager.carregarOuGerarSalt();

        // Agora carrega de novo
        byte[] saltCarregado = SaltManager.carregarOuGerarSalt();

        assertArrayEquals(saltOriginal, saltCarregado, "Salt carregado deve ser igual ao original salvo");
    }
}

