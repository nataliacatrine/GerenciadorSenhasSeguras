package app;

import model.Credencial;
import repository.RepositorioCredenciais;
import service.*;
import util.*;
import javax.crypto.SecretKey;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;

/**
 * Classe principal do Gerenciador de Senhas.
 * <p>
 * Este aplicativo realiza autenticação com senha mestra e verificação 2FA,
 * e permite ao usuário gerenciar credenciais de serviços (listar, adicionar, remover).
 * As senhas são criptografadas com AES-GCM usando chaves derivadas da senha mestra.
 * </p>
 */
public class Main {

    /**
     * Método principal que inicia o gerenciador de senhas.
     *
     * @param args Argumentos da linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SecretKey chaveMestra = null;

        try {
            // Verifica se já existe senha mestra definida
            if (!SenhaMestra.existeSenhaMestra()) {
                System.out.println("Nenhuma senha mestra definida.");
                System.out.print("Defina uma nova senha mestra: ");
                String novaSenha = scanner.nextLine();
                SenhaMestra.definirNovaSenha(novaSenha);
                System.out.println("Senha mestra criada com sucesso!");
            }

            // Processo de autenticação com senha mestra + 2FA
            boolean autenticado = false;
            int tentativas = 5;

            while (!autenticado && tentativas > 0) {
                System.out.print("Digite a senha mestra: ");
                String senhaMestraStr = scanner.nextLine();

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

                            // Deriva a chave mestra
                            byte[] salt = SaltManager.carregarOuGerarSalt();
                            chaveMestra = CriptografiaChaves.derivarChaveMestra(senhaMestraStr.toCharArray(), salt);

                            // Limpa senha da memória
                            Arrays.fill(senhaMestraStr.toCharArray(), '\0');

                        } else {
                            tentativas--;
                            System.out.println("Token 2FA inválido. Tentativas restantes: " + tentativas);
                        }
                    } catch (NumberFormatException e) {
                        tentativas--;
                        System.out.println("Token inválido. Digite apenas números. Tentativas restantes: " + tentativas);
                    }
                } else {
                    tentativas--;
                    System.out.println("Senha mestra incorreta. Tentativas restantes: " + tentativas);
                }
            }

            if (!autenticado) {
                System.out.println("Acesso negado. Encerrando o programa.");
                scanner.close();
                return;
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar a senha mestra ou 2FA: " + e.getMessage());
            scanner.close();
            return;
        }

        // Carrega as credenciais criptografadas
        List<Credencial> credenciais = RepositorioCredenciais.carregar();
        System.out.println("Gerenciador de Senhas iniciado!");
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
                    // Lista credenciais descriptografadas
                    if (credenciais.isEmpty()) {
                        System.out.println("Nenhuma credencial cadastrada.");
                    } else {
                        for (int i = 0; i < credenciais.size(); i++) {
                            Credencial c = credenciais.get(i);
                            try {
                                String senhaDescriptografada = c.getSenhaDescriptografada(chaveMestra);
                                System.out.printf("%d) Serviço: %s | Usuário: %s | Senha: %s\n",
                                        i + 1,
                                        c.getNomeServico(),
                                        c.getUsuario(),
                                        senhaDescriptografada);
                            } catch (Exception e) {
                                System.out.printf("%d) Serviço: %s | Usuário: %s | Senha: <erro>\n",
                                        i + 1,
                                        c.getNomeServico(),
                                        c.getUsuario());
                            }
                        }
                    }
                    break;

                case "2":
                    // Adiciona nova credencial
                    System.out.print("Nome do serviço: ");
                    String servico = scanner.nextLine();

                    System.out.print("Usuário: ");
                    String usuario = scanner.nextLine();

                    System.out.print("Senha (deixe em branco para gerar): ");
                    String senha = scanner.nextLine();

                    if (senha.trim().isEmpty()) {
                        senha = GeradorSenha.gerarSenha(12);
                        System.out.println("Senha gerada: " + senha);
                    }

                    // Verifica vazamento
                    boolean senhaVazada;
                    try {
                        senhaVazada = VerificadorVazamentoSenha.senhaVazada(senha);
                    } catch (Exception e) {
                        System.out.println("Não foi possível verificar vazamento da senha (sem conexão). Prosseguindo sem verificação.");
                        senhaVazada = false;
                    }

                    while (senhaVazada) {
                        System.out.println("Essa senha já foi vazada. Escolha outra.");
                        System.out.print("Nova senha (ou deixe em branco para gerar): ");
                        senha = scanner.nextLine();
                        if (senha.trim().isEmpty()) {
                            senha = GeradorSenha.gerarSenha(12);
                            System.out.println("Senha gerada: " + senha);
                        }
                        try {
                            senhaVazada = VerificadorVazamentoSenha.senhaVazada(senha);
                        } catch (Exception e) {
                            System.out.println("Não foi possível verificar vazamento da senha (sem conexão). Prosseguindo sem verificação.");
                            senhaVazada = false;
                        }
                    }

                    Credencial nova = new Credencial(servico, usuario, senha, chaveMestra);
                    credenciais.add(nova);
                    RepositorioCredenciais.salvar(credenciais);
                    System.out.println("Credencial adicionada com sucesso.");
                    break;

                case "3":
                    // Remove credencial selecionada
                    if (credenciais.isEmpty()) {
                        System.out.println("Nenhuma credencial para remover.");
                    } else {
                        System.out.print("Número da credencial a remover: ");
                        try {
                            int idx = Integer.parseInt(scanner.nextLine()) - 1;
                            if (idx >= 0 && idx < credenciais.size()) {
                                Credencial removida = credenciais.remove(idx);
                                RepositorioCredenciais.salvar(credenciais);
                                System.out.println("Removida: " + removida.getNomeServico());
                            } else {
                                System.out.println("Índice inválido.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada inválida. Digite um número.");
                        }
                    }
                    break;

                case "0":
                    // Encerra o programa
                    executando = false;
                    System.out.println("Encerrando o gerenciador.");
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }

        scanner.close();
    }
}
