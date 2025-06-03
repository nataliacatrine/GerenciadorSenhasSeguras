package model;

import org.junit.jupiter.api.Test;
import service.CriptografiaChaves;
import service.SaltManager;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

public class CredencialTest {

    @Test
    public void testCriaCredencialERecuperaSenha() throws Exception {
        String nome = "Gmail";
        String usuario = "usuario@gmail.com";
        String senhaOriginal = "senhaForte@123";

        char[] senhaMestra = "senhaMestraUnica".toCharArray();
        byte[] salt = SaltManager.gerarNovoSalt();
        SecretKey chaveMestra = CriptografiaChaves.derivarChaveMestra(senhaMestra, salt);

        Credencial credencial = new Credencial(nome, usuario, senhaOriginal, chaveMestra);
        String senhaDescriptografada = credencial.getSenhaDescriptografada(chaveMestra);

        assertEquals(senhaOriginal, senhaDescriptografada);
    }
}