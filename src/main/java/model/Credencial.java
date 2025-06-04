package model;

import service.CriptografiaChaves;

import javax.crypto.SecretKey;

/**
 * Representa uma credencial de acesso a um serviço (ex: email, site, etc.).
 *
 * Cada credencial possui uma senha criptografada com uma chave AES única,
 * e essa chave AES é protegida com a chave mestra do usuário.
 */
public class Credencial {

    /** Nome do serviço (ex: "Gmail", "Facebook") */
    private String nomeServico;

    /** Nome de usuário ou email utilizado para login no serviço */
    private String usuario;

    /** Senha criptografada com AES-GCM e codificada em Base64 (IV embutido) */
    private String senhaCriptografada;

    /** Chave AES criptografada com a chave mestra (Base64 com IV embutido) */
    private String chaveAESCriptografada;

    /** Construtor vazio (requerido para serialização) */
    public Credencial() {}

    /**
     * Construtor para criação de uma nova credencial.
     * <p>
     * Gera uma nova chave AES, criptografa a senha com ela, e protege essa chave AES
     * com a chave mestra fornecida.
     *
     * @param nomeServico Nome do serviço (não pode ser nulo ou vazio)
     * @param usuario     Nome de usuário (não pode ser nulo ou vazio)
     * @param senha       Senha em texto puro que será criptografada
     * @param chaveMestra Chave mestra para proteger a chave AES
     */
    public Credencial(String nomeServico, String usuario, String senha, SecretKey chaveMestra) {
        if (nomeServico == null || nomeServico.trim().isEmpty()) {
            throw new RuntimeException("nomeServico não pode ser nulo ou vazio");
        }
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new RuntimeException("usuario não pode ser nulo ou vazio");
        }
        if (chaveMestra == null) {
            throw new RuntimeException("chaveMestra não pode ser nula");
        }

        this.nomeServico = nomeServico;
        this.usuario = usuario;

        try {
            SecretKey chaveAES = CriptografiaChaves.gerarChaveAES();
            this.senhaCriptografada = CriptografiaChaves.criptografarTextoComAES(senha, chaveAES);
            this.chaveAESCriptografada = CriptografiaChaves.criptografarChaveAES(chaveAES, chaveMestra);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar ou criptografar credencial", e);
        }
    }

    /** @return o nome do serviço associado a esta credencial */
    public String getNomeServico() {
        return nomeServico;
    }

    /** @param nomeServico Define o nome do serviço */
    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    /** @return o nome de usuário da credencial */
    public String getUsuario() {
        return usuario;
    }

    /** @param usuario Define o nome de usuário da credencial */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /** @return a senha criptografada (Base64 com IV embutido) */
    public String getSenhaCriptografada() {
        return senhaCriptografada;
    }

    /** @param senhaCriptografada Define a senha criptografada (Base64 com IV embutido) */
    public void setSenhaCriptografada(String senhaCriptografada) {
        this.senhaCriptografada = senhaCriptografada;
    }

    /** @return a chave AES criptografada com a chave mestra (Base64 com IV embutido) */
    public String getChaveAESCriptografada() {
        return chaveAESCriptografada;
    }

    /** @param chaveAESCriptografada Define a chave AES criptografada */
    public void setChaveAESCriptografada(String chaveAESCriptografada) {
        this.chaveAESCriptografada = chaveAESCriptografada;
    }

    /**
     * Descriptografa a senha usando a chave mestra fornecida.
     * <p>
     * Primeiro, a chave AES usada originalmente é recuperada via descriptografia
     * com a chave mestra, e depois usada para descriptografar a senha.
     *
     * @param chaveMestra Chave mestra usada para recuperar a chave AES
     * @return Senha em texto puro
     * @throws RuntimeException se a descriptografia falhar
     */
    public String getSenhaDescriptografada(SecretKey chaveMestra) {
        try {
            SecretKey chaveAES = CriptografiaChaves.descriptografarChaveAES(chaveAESCriptografada, chaveMestra);
            return CriptografiaChaves.descriptografarTextoComAES(senhaCriptografada, chaveAES);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar a senha", e);
        }
    }
}
