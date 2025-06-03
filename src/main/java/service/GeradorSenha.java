package service;

import java.security.SecureRandom;

public class GeradorSenha {

    // Caracteres permitidos na senha
    private static final String MAIUSCULAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String MINUSCULAS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMEROS = "0123456789";
    private static final String SIMBOLOS = "!@#$%^&*()-_+=<>?";

    private static final String TODOS_CARACTERES = MAIUSCULAS + MINUSCULAS + NUMEROS + SIMBOLOS;

    private static final SecureRandom random = new SecureRandom();

    public static String gerarSenha(int tamanho) {
        if (tamanho < 6) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
        }

        StringBuilder senha = new StringBuilder(tamanho);

        // Garantir pelo menos um caractere de cada tipo
        senha.append(MAIUSCULAS.charAt(random.nextInt(MAIUSCULAS.length())));
        senha.append(MINUSCULAS.charAt(random.nextInt(MINUSCULAS.length())));
        senha.append(NUMEROS.charAt(random.nextInt(NUMEROS.length())));
        senha.append(SIMBOLOS.charAt(random.nextInt(SIMBOLOS.length())));

        // Preencher o resto com caracteres aleatórios
        for (int i = 4; i < tamanho; i++) {
            senha.append(TODOS_CARACTERES.charAt(random.nextInt(TODOS_CARACTERES.length())));
        }

        // Embaralhar para não ficar sempre na ordem
        return embaralharString(senha.toString());
    }

    private static String embaralharString(String input) {
        char[] a = input.toCharArray();
        for (int i = a.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = a[i];
            a[i] = a[j];
            a[j] = temp;
        }
        return new String(a);
    }
}