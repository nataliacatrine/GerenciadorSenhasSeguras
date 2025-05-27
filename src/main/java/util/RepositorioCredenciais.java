package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Credencial;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCredenciais {
    private static final String ARQUIVO = "credenciais.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void salvar(List<Credencial> credenciais, SecretKey chaveMestra) {
        // Antes de salvar, criptografar cada chaveAESBase64 com a chave mestra
        for (Credencial cred : credenciais) {
            try {
                String chaveAESBase64 = cred.getChaveAESBase64();
                // Aqui a chaveAESBase64 está na forma original (base64 da chave AES),
                // então converte para SecretKey
                SecretKey chaveAES = CriptografiaAES.base64ParaChave(chaveAESBase64);

                // Criptografa a chave AES com a chave mestra
                String chaveCriptografada = CriptografiaChaves.criptografarChaveAES(chaveAES, chaveMestra);

                // Sobrescreve com chave criptografada
                cred.setChaveAESBase64(chaveCriptografada);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro ao criptografar a chave AES da credencial: " + cred.getNomeServico());
            }
        }

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ARQUIVO), credenciais);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Credencial> carregar(SecretKey chaveMestra) {
        File file = new File(ARQUIVO);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            List<Credencial> credenciais = mapper.readValue(file, new TypeReference<List<Credencial>>() {});

            // Após carregar, descriptografar cada chaveAESBase64
            for (Credencial cred : credenciais) {
                try {
                    SecretKey chaveAES = CriptografiaChaves.descriptografarChaveAES(cred.getChaveAESBase64(), chaveMestra);
                    String chaveAESBase64 = CriptografiaAES.chaveParaBase64(chaveAES);
                    cred.setChaveAESBase64(chaveAESBase64); // Substitui pela chave original em Base64
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Erro ao descriptografar a chave AES da credencial: " + cred.getNomeServico());
                }
            }
            return credenciais;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
