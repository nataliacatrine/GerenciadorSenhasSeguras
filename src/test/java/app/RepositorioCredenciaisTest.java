package app;

import model.Credencial;
import org.junit.jupiter.api.*;
import util.RepositorioCredenciais;
import util.SenhaMestra;

import javax.crypto.SecretKey;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepositorioCredenciaisTest {

    private static final String SENHA_TESTE = "senha123";
    private static SecretKey chaveMestra;
    private static List<Credencial> listaCredenciais;

    @BeforeAll
    public static void setup() throws Exception {
        // Define a senha mestra de teste
        SenhaMestra.definirNovaSenha(SENHA_TESTE);
        chaveMestra = SenhaMestra.obterChaveMestra(SENHA_TESTE);

        // Cria lista de credenciais
        listaCredenciais = new ArrayList<>();
        listaCredenciais.add(new Credencial("Email", "usuario1", "senha1", chaveMestra));
        listaCredenciais.add(new Credencial("Banco", "usuario2", "senha2", chaveMestra));
    }

    @Test
    @Order(1)
    public void testSalvarCredenciais() {
        RepositorioCredenciais.salvar(listaCredenciais);
        File arquivo = new File("credenciais.json");
        assertTrue(arquivo.exists(), "Arquivo de credenciais.json deveria ter sido criado.");
    }

    @Test
    @Order(2)
    public void testCarregarCredenciais() {
        List<Credencial> carregadas = RepositorioCredenciais.carregar();
        assertNotNull(carregadas);
        assertEquals(2, carregadas.size());
    }

    @Test
    @Order(3)
    public void testCredencialDescriptografadaCorretamente() {
        List<Credencial> carregadas = RepositorioCredenciais.carregar();
        Credencial c = carregadas.get(0);
        String senhaDescriptografada = c.getSenhaDescriptografada(chaveMestra);

        assertEquals("senha1", senhaDescriptografada);
    }

    @AfterAll
    public static void cleanup() {
        File arquivo = new File("credenciais.json");
        if (arquivo.exists()) {
            arquivo.delete();
        }
        File hash = new File("senha_mestra.hash");
        if (hash.exists()) {
            hash.delete();
        }
    }
}