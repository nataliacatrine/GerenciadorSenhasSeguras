package util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Credencial;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RepositorioCredenciais {
    private static final String ARQUIVO = "credenciais.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void salvar(List<Credencial> credenciais) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ARQUIVO), credenciais);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Credencial> carregar() {
        File file = new File(ARQUIVO);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return mapper.readValue(file, new TypeReference<List<Credencial>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}