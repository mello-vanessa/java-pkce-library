package br.com.vanessacardoso.pkce;

import br.com.vanessacardoso.pkce.internal.Base64Encoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Main {
    public static void main(String[] args)  {
        SecureRandom numeroRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        numeroRandom.nextBytes(bytes); //gerando conjunto bruto de bytes
        System.out.println("SecureRandom: "+Arrays.toString(bytes));//visualizando ele

        /*
        getUrlEncoder: Retorna um valor Base64.Encoderque codifica usando o esquema de codificação
        base64 seguro para URLs e nomes de arquivos .
         */
        String codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        /*
        withoutPadding().encodeToString() é na Base64.encoder
        withoutPadding():Retorna uma instância de codificador que codifica de forma equivalente a esta,
        mas sem adicionar nenhum caractere de preenchimento ao final dos dados de byte codificados.
        encodeToString(): Codifica a matriz de bytes especificada em uma String usando o Base64 esquema de codificação.
         */
        System.out.println("Code Verifier: "+codeVerifier);
        //passar o tipo de algoritmo que eu vou usar
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            //Criar o code challenge
            byte[] codeChallengeBytes = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(codeChallengeBytes);
            System.out.println("Code Challenge: "+codeChallenge);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
