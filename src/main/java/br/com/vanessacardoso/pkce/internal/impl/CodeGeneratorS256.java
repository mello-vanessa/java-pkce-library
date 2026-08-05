package br.com.vanessacardoso.pkce.internal.impl;

import br.com.vanessacardoso.pkce.internal.ACodeGenerator;
import br.com.vanessacardoso.pkce.internal.Base64Encoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CodeGeneratorS256 extends ACodeGenerator {

    @Override
    public String generateCodeChallenge(String codeVerifier){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] codeChallengeBytes = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64Encoder.encodeUrlSafe(codeChallengeBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
