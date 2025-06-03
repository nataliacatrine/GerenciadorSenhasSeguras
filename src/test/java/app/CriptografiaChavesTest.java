package util;

import org.junit.jupiter.api.Test;
import javax.crypto.SecretKey;
import static org.junit.jupiter.api.Assertions.*;

public class CriptografiaChavesTest {

    @Test
    public void testCriptografarEDescriptografarTextoComAES() throws Exception {
        String textoOriginal = "senhaSuperSegura123!";
        SecretKey chaveAES = CriptografiaChaves.gerarChaveAES();

        String criptografado = CriptografiaChaves.criptografarTextoComAES(textoOriginal, chaveAES);
        String descriptografado = CriptografiaChaves.descriptografarTextoComAES(criptografado, chaveAES);

        assertEquals(textoOriginal, descriptografado);
    }

    @Test
    public void testCriptografarEDescriptografarChaveAESComChaveMestra() throws Exception {
        SecretKey chaveAES = CriptografiaChaves.gerarChaveAES();
        char[] senhaMestra = "senhaMestraTeste".toCharArray();
        byte[] salt = SaltManager.gerarNovoSalt();
        SecretKey chaveMestra = CriptografiaChaves.derivarChaveMestra(senhaMestra, salt);

        String chaveCriptografada = CriptografiaChaves.criptografarChaveAES(chaveAES, chaveMestra);
        SecretKey chaveDescriptografada = CriptografiaChaves.descriptografarChaveAES(chaveCriptografada, chaveMestra);

        assertNotNull(chaveDescriptografada);
    }
}
