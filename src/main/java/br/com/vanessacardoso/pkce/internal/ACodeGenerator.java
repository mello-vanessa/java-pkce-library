package br.com.vanessacardoso.pkce.internal;

import java.security.SecureRandom;

public abstract class ACodeGenerator implements ICodeGenerator{
    @Override
    public String generateCodeVerifier(){
        SecureRandom numeroRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        numeroRandom.nextBytes(bytes);
        return Base64Encoder.encodeUrlSafe(bytes);
    }

    @Override
    public abstract String generateCodeChallenge(String codeVerifier);
}
