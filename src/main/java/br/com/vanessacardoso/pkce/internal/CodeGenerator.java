package br.com.vanessacardoso.pkce.internal;

public interface CodeGenerator {
    public String generateCodeVerifier();
    public String generateCodeChallenge(String codeVerifier);
}
