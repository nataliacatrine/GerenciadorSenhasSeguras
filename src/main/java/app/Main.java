package app;

import model.Credencial;
import util.Autenticador2FA;
import util.Chave2FAUtil;
import util.GeradorSenha;
import util.RepositorioCredenciais;
import util.SenhaMestra;
import util.VerificadorVazamentoSenha;
import util.CriptografiaChaves;
import util.SaltManager;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SecretKey chaveMestra = null;

        try {
            if (!SenhaMestra.existeSenhaMestra()) {
                System.out.println("Nenhuma senha mestra definida.");
                System.out.print("Defina uma nova senha mestra: ");
                String novaSenha = scanner.nextLine();
                SenhaMestra.definirNovaSenha(novaSenha);
                System.out.println("Senha mestra criada com sucesso!");
            }

            boolean autenticado = false;
            int tentativas = 5;
            String senhaMestraStr = null;

            while (!autenticado && tentativas > 0) {
                System.out.print("Digite a senha mestra: ");
                senhaMestraStr = scanner.nextLine();

                if (SenhaMestra.verificarSenha(senhaMestraStr)) {
                    String chave2FA = Chave2FAUtil.carregarChave();
                    if (chave2FA == null) {
                        chave2FA = Autenticador2FA.gerarChaveSecreta();
                        Chave2FAUtil.salvarChave(chave2FA);
                        System.out.println("Configure seu app Google Authenticator com esta chave secreta:");
                        System.out.println(chave2FA);
                        System.out.println("Após configurar, digite o token 2FA gerado.");
                    }

                    System.out.print("Digite o token 2FA gerado no app: ");
                    String tokenStr = scanner.nextLine();

                    try {
                        int token = Integer.parseInt(tokenStr);
                        boolean valido = Autenticador2FA.validarToken(chave2FA, token);
                        if (valido) {
                            autenticado = true;
                            System.out.println("Senha mestra e 2FA validados. Acesso concedido.");

                            // Carregar ou gerar salt para derivar a chave mestra
                            byte[] salt = SaltManager.carregarOuGerarSalt();
                            chaveMestra = CriptografiaChaves.derivarChaveMestra(senhaMestraStr.toCharArray(), salt);

                            // Limpar senha da memória
                            Arrays.fill(senhaMestraStr.toCharArray(), '\0');

                        } else {
                            tentativas--;
                            System.out.println("Token 2FA inválido. Tentativas restantes: " + tentativas);
                        }
                    } catch (NumberFormatException e) {
                        tentativas--;
                        System.out.println("Token 2FA inválido. Informe apenas números. Tentativas restantes: " + tentativas);
                    }
                } else {
                    tentativas--;
                    System.out.println("Senha incorreta. Tentativas restantes: " + tentativas);
                }
            }

            if (!autenticado) {
                System.out.println("Acesso negado. Encerrando o programa.");
                scanner.close();
                return;
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar a senha mestra ou chave 2FA: " + e.getMessage());
            scanner.close();
            return;
        }

        // Carregar credenciais com chave mestra derivada
        List<Credencial> credenciais = RepositorioCredenciais.carregar(chaveMestra);

        System.out.println("Gerenciador de Senhas Seguras iniciado!");
        System.out.println("Credenciais carregadas: " + credenciais.size());

        boolean executando = true;
        while (executando) {
            System.out.println("\n=== MENU ===");
            System.out.println("1 - Listar credenciais");
            System.out.println("2 - Adicionar nova credencial");
            System.out.println("3 - Remover credencial");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    if (credenciais.isEmpty()) {
                        System.out.println("Nenhuma credencial cadastrada.");
                    } else {
                        System.out.println("Credenciais:");
                        for (int i = 0; i < credenciais.size(); i++) {
                            Credencial c = credenciais.get(i);
                            try {
                                String senhaDescriptografada = c.getSenhaDescriptografada(chaveMestra);
                                System.out.printf("%d) Serviço: %s, Usuário: %s, Senha: %s\n",
                                        i + 1,
                                        c.getNomeServico(),
                                        c.getUsuario(),
                                        senhaDescriptografada);
                            } catch (Exception e) {
                                System.out.printf("%d) Serviço: %s, Usuário: %s, Senha: <erro ao descriptografar>\n",
                                        i + 1, c.getNomeServico(), c.getUsuario());
                            }
                        }
                    }
                    break;

                case "2":
                    System.out.print("Nome do serviço: ");
                    String servico = scanner.nextLine();

                    System.out.print("Usuário: ");
                    String usuario = scanner.nextLine();

                    System.out.print("Senha (deixe vazio para gerar senha segura): ");
                    String senha = scanner.nextLine();

                    if (senha == null || senha.trim().isEmpty()) {
                        senha = GeradorSenha.gerarSenha(12);
                        System.out.println("Senha segura gerada: " + senha);
                    }

                    while (VerificadorVazamentoSenha.senhaVazada(senha)) {
                        System.out.println("Essa senha já foi vazada em algum vazamento público. Por favor, escolha outra senha.");
                        System.out.print("Digite uma nova senha (ou deixe vazio para gerar uma nova senha segura): ");
                        senha = scanner.nextLine();
                        if (senha == null || senha.trim().isEmpty()) {
                            senha = GeradorSenha.gerarSenha(12);
                            System.out.println("Senha segura gerada: " + senha);
                        }
                    }

                    Credencial novaCred = new Credencial(servico, usuario, senha, chaveMestra);
                    credenciais.add(novaCred);
                    RepositorioCredenciais.salvar(credenciais, chaveMestra);
                    System.out.println("Credencial salva com sucesso!");
                    break;

                case "3":
                    if (credenciais.isEmpty()) {
                        System.out.println("Nenhuma credencial para remover.");
                    } else {
                        System.out.print("Digite o número da credencial a remover: ");
                        try {
                            int idx = Integer.parseInt(scanner.nextLine()) - 1;
                            if (idx >= 0 && idx < credenciais.size()) {
                                Credencial removida = credenciais.remove(idx);
                                RepositorioCredenciais.salvar(credenciais, chaveMestra);
                                System.out.println("Credencial removida: " + removida.getNomeServico());
                            } else {
                                System.out.println("Número inválido.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada inválida. Informe um número.");
                        }
                    }
                    break;

                case "0":
                    executando = false;
                    System.out.println("Saindo do gerenciador. Até logo!");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }

        scanner.close();
    }
}