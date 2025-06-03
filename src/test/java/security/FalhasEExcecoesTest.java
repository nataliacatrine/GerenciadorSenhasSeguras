package security;

import model.Credencial;
import org.junit.jupiter.api.*;
import repository.RepositorioCredenciais;
import service.SenhaMestra;
import service.SaltManager;
import service.CriptografiaChaves;

import javax.crypto.SecretKey;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FalhasEExcecoesTest {

    private static final String SENHA_CORRETA = "SenhaForte123!";
    private static final String SENHA_ERRADA = "SenhaErrada!";
    private SecretKey chaveCorreta;
    private SecretKey chaveErrada;
    private byte[] salt;

    private final Path arquivoOriginal = Paths.get("credenciais.json");
    private final Path arquivoBackup = Paths.get("credenciais_backup.json");

    @BeforeAll
    void backupArquivoOriginal() throws IOException {
        if (Files.exists(arquivoOriginal)) {
            Files.copy(arquivoOriginal, arquivoBackup, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @BeforeEach
    void setup() throws Exception {
        SenhaMestra.definirNovaSenha(SENHA_CORRETA);
        salt = SaltManager.carregarOuGerarSalt();

        chaveCorreta = CriptografiaChaves.derivarChaveMestra(SENHA_CORRETA.toCharArray(), salt);
        chaveErrada = CriptografiaChaves.derivarChaveMestra(SENHA_ERRADA.toCharArray(), salt);

        // Sempre sobrescreve credenciais.json com credencial válida antes de cada teste
        List<Credencial> lista = new ArrayList<>();
        lista.add(new Credencial("site.com", "user", "senha123", chaveCorreta));
        RepositorioCredenciais.salvar(lista);
    }

    @Test
    @DisplayName("Deve impedir descriptografia com chave mestra incorreta")
    void testDescriptografiaComChaveErrada() {
        List<Credencial> lista = RepositorioCredenciais.carregar();
        assertFalse(lista.isEmpty(), "Deve carregar ao menos uma credencial");

        Credencial cred = lista.get(0);

        assertThrows(RuntimeException.class, () -> {
            cred.getSenhaDescriptografada(chaveErrada);
        }, "O sistema deve lançar exceção ao usar chave incorreta para descriptografar");
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao carregar JSON corrompido")
    void testArquivoJsonCorrompido() throws IOException {
        try (FileWriter writer = new FileWriter(arquivoOriginal.toFile())) {
            writer.write("{{ json inválido ::: ...");
        }

        List<Credencial> resultado = RepositorioCredenciais.carregar();
        assertNotNull(resultado, "Mesmo com JSON corrompido, a lista não deve ser nula");
        assertTrue(resultado.isEmpty(), "Com JSON corrompido, a lista deve ser vazia");
    }

    @Test
    @DisplayName("Deve impedir criação de Credencial com campos nulos (exceto senha)")
    void testCriarCredencialComCamposNulos() {
        assertThrows(RuntimeException.class, () -> {
            new Credencial(null, "usuario", "senha", chaveCorreta);
        }, "O sistema deve impedir criação de credencial com nomeServico nulo");

        assertThrows(RuntimeException.class, () -> {
            new Credencial("site.com", null, "senha", chaveCorreta);
        }, "O sistema deve impedir criação de credencial com usuario nulo");

        // senha pode ser nula (gera senha aleatória), logo não testa exceção para senha nula
    }

    @Test
    @DisplayName("Deve impedir criação de Credencial com campos vazios ou em branco (exceto senha)")
    void testCriarCredencialComCamposVaziosOuBrancos() {
        assertThrows(RuntimeException.class, () -> {
            new Credencial("", "usuario", "senha", chaveCorreta);
        }, "O sistema deve impedir criação de credencial com nomeServico vazio");

        assertThrows(RuntimeException.class, () -> {
            new Credencial("site.com", "", "senha", chaveCorreta);
        }, "O sistema deve impedir criação de credencial com usuario vazio");

        assertThrows(RuntimeException.class, () -> {
            new Credencial("   ", "usuario", "senha", chaveCorreta);
        }, "O sistema deve impedir criação de credencial com nomeServico só espaços");

        assertThrows(RuntimeException.class, () -> {
            new Credencial("site.com", "   ", "senha", chaveCorreta);
        }, "O sistema deve impedir criação de credencial com usuario só espaços");

        // senha pode ser vazia ou só espaços (gera senha aleatória), não testa exceção para senha
    }

    @Test
    @DisplayName("Deve impedir criação de Credencial com chave de criptografia nula")
    void testCriarCredencialComChaveNula() {
        assertThrows(RuntimeException.class, () -> {
            new Credencial("site.com", "usuario", "senha", null);
        }, "O sistema deve impedir criação de credencial com chave de criptografia nula");
    }

    @AfterAll
    void restaurarArquivoOriginal() throws IOException {
        SenhaMestra.apagarSenhaMestra();

        if (Files.exists(arquivoOriginal)) {
            Files.delete(arquivoOriginal);
        }

        if (Files.exists(arquivoBackup)) {
            Files.move(arquivoBackup, arquivoOriginal, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

