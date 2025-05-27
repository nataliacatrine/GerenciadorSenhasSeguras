package app;

import util.VerificadorVazamentoSenha;

public class TestaVerificador {
    public static void main(String[] args) {
        String senha = "123456"; // Senha que vamos testar

        try {
            boolean vazada = VerificadorVazamentoSenha.senhaVazada(senha);
            if (vazada) {
                System.out.println("A senha '" + senha + "' foi encontrada em vazamentos. Use outra senha!");
            } else {
                System.out.println("A senha '" + senha + "' NÃO foi encontrada em vazamentos. Pode usar com mais segurança.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao verificar vazamento da senha: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
