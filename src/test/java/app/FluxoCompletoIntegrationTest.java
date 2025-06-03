package app;

import model.Credencial;
import repository.RepositorioCredenciais;
import service.SenhaMestra;

import org.junit.jupiter.api.*;
import javax.crypto.SecretKey;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FluxoCompletoIntegrationTest {

    private static final String SENHA_MESTRA_TESTE = "SenhaForte123!";

    @BeforeAll
    void setup() throws Exception {
        SenhaMestra.apagarSenhaMestra();
        File credFile = new File("credenciais.json");
        if (credFile.exists()) {
            credFile.delete();
        }
    }

    @Test
    @DisplayName("Fluxo Completo: definir senha mestra, salvar, carregar, modificar e remover credenciais")
    void fluxoCompleto() throws Exception {
        // 1. Definir nova senha mestra
        SenhaMestra.definirNovaSenha(SENHA_MESTRA_TESTE);
        assertTrue(SenhaMestra.existeSenhaMestra(), "Senha mestra deve existir após definir");

        // 2. Verificar senha mestra
        assertTrue(SenhaMestra.verificarSenha(SENHA_MESTRA_TESTE), "Senha mestra deve ser verificada com sucesso");
        assertFalse(SenhaMestra.verificarSenha("senhaErrada"), "Senha incorreta deve falhar a verificação");

        // 3. Obter chave mestra
        SecretKey chaveMestra = SenhaMestra.obterChaveMestra(SENHA_MESTRA_TESTE);
        assertNotNull(chaveMestra, "Chave mestra não deve ser nula");

        // 4. Criar lista de credenciais usando construtor com chaveMestra
        List<Credencial> listaCredenciais = new ArrayList<>();
        Credencial cred1 = new Credencial("gmail.com", "usuario@gmail.com", "senhaGmail123", chaveMestra);
        Credencial cred2 = new Credencial("facebook.com", "user.fb", "senhaFB456", chaveMestra);
        listaCredenciais.add(cred1);
        listaCredenciais.add(cred2);

        // 5. Salvar credenciais no arquivo JSON
        RepositorioCredenciais.salvar(listaCredenciais);

        // 6. Carregar credenciais do arquivo
        List<Credencial> listaCarregada = RepositorioCredenciais.carregar();
        assertEquals(2, listaCarregada.size(), "Deve carregar duas credenciais");

        // 7. Validar dados da primeira credencial (descriptografar senha)
        Credencial c1 = listaCarregada.stream()
                .filter(c -> c.getNomeServico().equals("gmail.com"))
                .findFirst()
                .orElse(null);
        assertNotNull(c1);
        assertEquals("usuario@gmail.com", c1.getUsuario());
        assertEquals("senhaGmail123", c1.getSenhaDescriptografada(chaveMestra));

        // 8. Editar uma credencial: remover antiga e adicionar nova com senha atualizada
        listaCarregada.remove(c1);
        Credencial c1Atualizado = new Credencial("gmail.com", "usuario@gmail.com", "novaSenhaGmail!", chaveMestra);
        listaCarregada.add(c1Atualizado);
        RepositorioCredenciais.salvar(listaCarregada);

        // 9. Recarregar e verificar atualização
        List<Credencial> listaAtualizada = RepositorioCredenciais.carregar();

        Credencial c1Rec = listaAtualizada.stream()
                .filter(c -> c.getNomeServico().equals("gmail.com"))
                .findFirst()
                .orElse(null);

        assertNotNull(c1Rec);
        assertEquals("novaSenhaGmail!", c1Rec.getSenhaDescriptografada(chaveMestra));

        // 10. Remover a primeira credencial da lista e salvar novamente
        listaAtualizada.removeIf(c -> c.getNomeServico().equals("gmail.com"));
        RepositorioCredenciais.salvar(listaAtualizada);

        // 11. Recarregar e verificar remoção
        List<Credencial> listaFinal = RepositorioCredenciais.carregar();
        assertEquals(1, listaFinal.size(), "Deve restar apenas uma credencial após remoção");
        assertFalse(listaFinal.stream().anyMatch(c -> c.getNomeServico().equals("gmail.com")));
    }

    @AfterAll
    void cleanup() {
        SenhaMestra.apagarSenhaMestra();
        File credFile = new File("credenciais.json");
        if (credFile.exists()) {
            credFile.delete();
        }
    }
}
