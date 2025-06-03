package model;

import service.CriptografiaChaves;

import javax.crypto.SecretKey;

public class Credencial {
    private String nomeServico;
    private String usuario;
    private String senhaCriptografada;       // Senha criptografada com AES-GCM (Base64 com IV embutido)
    private String chaveAESCriptografada;    // Chave AES criptografada com chave mestra (Base64 com IV embutido)

    public Credencial() {}

    /**
     * Construtor usado ao criar uma nova credencial.
     * Gera uma chave AES individual, criptografa a senha com ela e
     * criptografa a chave AES com a chave mestra fornecida.
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

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenhaCriptografada() {
        return senhaCriptografada;
    }

    public void setSenhaCriptografada(String senhaCriptografada) {
        this.senhaCriptografada = senhaCriptografada;
    }

    public String getChaveAESCriptografada() {
        return chaveAESCriptografada;
    }

    public void setChaveAESCriptografada(String chaveAESCriptografada) {
        this.chaveAESCriptografada = chaveAESCriptografada;
    }

    /**
     * Descriptografa a senha utilizando a chave mestra fornecida.
     */
    public String getSenhaDescriptografada(SecretKey chaveMestra) {
        try {
            // 1. Descriptografa a chave AES com a chave mestra
            SecretKey chaveAES = CriptografiaChaves.descriptografarChaveAES(chaveAESCriptografada, chaveMestra);

            // 2. Descriptografa a senha com a chave AES
            return CriptografiaChaves.descriptografarTextoComAES(senhaCriptografada, chaveAES);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar a senha", e);
        }
    }
}
