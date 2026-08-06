package br.com.vanessacardoso.pkce.internal.impl;

import br.com.vanessacardoso.pkce.internal.AbstractCodeGenerator;
public class CodeGeneratorPlain extends AbstractCodeGenerator {
    @Override
    public String generateCodeChallenge(String codeVerifier) {
        return codeVerifier;
    }
}
