package br.com.vanessacardoso.pkce.internal.impl;

import br.com.vanessacardoso.pkce.internal.ACodeGenerator;
public class CodeGeneratorPlain extends ACodeGenerator {
    @Override
    public String generateCodeChallenge(String codeVerifier) {
        return codeVerifier;
    }
}
