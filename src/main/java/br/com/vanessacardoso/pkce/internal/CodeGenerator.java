package br.com.vanessacardoso.pkce.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CodeGenerator {
    private String verifier;

    //Retorna code_verifier
    public String generateRandomVerifier(){
        SecureRandom numeroRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        numeroRandom.nextBytes(bytes); //gerando conjunto bruto de bytes
        /*
        getUrlEncoder: Retorna um valor Base64.Encoder que codifica usando o esquema de codificação
        base64 seguro para URLs e nomes de arquivos.
        withoutPadding().encodeToString() é na Base64.encoder
        withoutPadding():Retorna uma instância de codificador que codifica de forma equivalente a esta,
        mas sem adicionar nenhum caractere de preenchimento ao final dos dados de byte codificados.
        encodeToString(): Codifica a matriz de bytes especificada em uma String usando o Base64 esquema de codificação.
        */
        return Base64Encoder.encodeUrlSafe(bytes);
    }
    //Retorna code_challenge
    public String generateS256Challenge(String codeVerifier){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //Criar o code challenge
            byte[] codeChallengeBytes = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64Encoder.encodeUrlSafe(codeChallengeBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
