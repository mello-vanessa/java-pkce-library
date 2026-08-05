package br.com.vanessacardoso.pkce.internal;

public interface ICodeGenerator {
    public String generateCodeVerifier();
    public String generateCodeChallenge(String codeVerifier);
}
