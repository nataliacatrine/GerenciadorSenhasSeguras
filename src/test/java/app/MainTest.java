package app;

import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import service.*;
import util.SaltManager;
import util.SenhaMestra;
import util.RepositorioCredenciais;
import service.Autenticador2FA;
import service.Chave2FAUtil;
import app.Main;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MainTest {

    @Test
    void testeFluxoBasicoComSenhaMestraE2FA() throws Exception {
        // Preparar dados simulados
        String senhaMestra = "senhaSegura123";
        String token2FA = "123456";

        // Simular entrada do usuário via Scanner
        String entradaUsuario = senhaMestra + "\n" + token2FA + "\n0\n"; // autentica e sai
        InputStream inputSimulado = new ByteArrayInputStream(entradaUsuario.getBytes());
        System.setIn(inputSimulado);

        // Capturar saída do sistema
        ByteArrayOutputStream saidaCapturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(saidaCapturada));

        // Preparar ambiente simulado
        SenhaMestra.definirNovaSenha(senhaMestra); // Define a senha mestra real

        // Salvar chave 2FA válida
        String chave2FA = Autenticador2FA.gerarChaveSecreta();
        Chave2FAUtil.salvarChave(chave2FA);

        // Gerar token 2FA válido no momento do teste
        int tokenValido = new com.warrenstrange.googleauth.GoogleAuthenticator().getTotpPassword(chave2FA);
        entradaUsuario = senhaMestra + "\n" + tokenValido + "\n0\n";
        System.setIn(new ByteArrayInputStream(entradaUsuario.getBytes()));

        // Salvar credenciais vazias para não causar erro
        RepositorioCredenciais.salvar(Arrays.asList());

        // Executar main
        Main.main(new String[0]);

        // Verificar se fluxo de autenticação ocorreu
        String saida = saidaCapturada.toString();
        assertTrue(saida.contains("Acesso concedido") || saida.contains("Gerenciador de Senhas iniciado!"));
    }
}
