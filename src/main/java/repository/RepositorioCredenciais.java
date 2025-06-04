package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.Credencial;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório responsável por salvar e carregar as credenciais em arquivo local.
 * <p>
 * As credenciais são armazenadas em formato JSON no arquivo {@code credenciais.json}
 * usando a biblioteca Jackson.
 */
public class RepositorioCredenciais {

    /** Caminho do arquivo JSON onde as credenciais são armazenadas */
    private static final String ARQUIVO = "credenciais.json";

    /** Instância do ObjectMapper para serialização/desserialização JSON */
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Salva a lista de credenciais no arquivo {@code credenciais.json}.
     *
     * @param credenciais Lista de objetos {@link Credencial} a serem salvos
     */
    public static void salvar(List<Credencial> credenciais) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ARQUIVO), credenciais);
        } catch (IOException e) {
            e.printStackTrace(); // Erro ao salvar o arquivo
        }
    }

    /**
     * Carrega a lista de credenciais a partir do arquivo {@code credenciais.json}.
     * <p>
     * Se o arquivo não existir ou ocorrer um erro de leitura, retorna uma lista vazia.
     *
     * @return Lista de {@link Credencial} lida do arquivo, ou lista vazia em caso de erro
     */
    public static List<Credencial> carregar() {
        File file = new File(ARQUIVO);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return mapper.readValue(file, new TypeReference<List<Credencial>>() {});
        } catch (IOException e) {
            e.printStackTrace(); // Erro ao ler o arquivo
            return new ArrayList<>();
        }
    }
}
