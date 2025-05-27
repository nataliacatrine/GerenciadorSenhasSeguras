package model;

import util.CriptografiaAES;
import util.CriptografiaChaves;

import javax.crypto.SecretKey;

public class Credencial {
    private String nomeServico;
    private String usuario;
    private String senhaCriptografada;
    private String chaveAESBase64; // chave AES criptografada (em Base64)

    // Construtor padrão para Jackson
    public Credencial() {}

    // Construtor ao criar nova credencial com a chave mestra
    public Credencial(String nomeServico, String usuario, String senha, SecretKey chaveMestra) {
        this.nomeServico = nomeServico;
        this.usuario = usuario;

        // Gera chave AES individual (SecretKey)
        SecretKey chaveAES = CriptografiaAES.gerarChaveAES();

        // Criptografa a senha com a chave AES
        this.senhaCriptografada = CriptografiaAES.encrypt(senha, CriptografiaAES.chaveParaBase64(chaveAES));

        try {
            // Criptografa a chave AES com a chave mestra e salva em Base64
            this.chaveAESBase64 = CriptografiaChaves.criptografarChaveAES(chaveAES, chaveMestra);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar chave AES com chave mestra", e);
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

    public String getChaveAESBase64() {
        return chaveAESBase64;
    }

    public void setChaveAESBase64(String chaveAESBase64) {
        this.chaveAESBase64 = chaveAESBase64;
    }

    // Método para descriptografar senha usando a chave mestra
    public String getSenhaDescriptografada(SecretKey chaveMestra) {
        try {
            // Descriptografa chave AES com a chave mestra
            SecretKey chaveAES = CriptografiaChaves.descriptografarChaveAES(chaveAESBase64, chaveMestra);
            // Descriptografa senha com chave AES (convertida para Base64)
            return CriptografiaAES.decrypt(senhaCriptografada, CriptografiaAES.chaveParaBase64(chaveAES));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao descriptografar senha", e);
        }
    }
}
