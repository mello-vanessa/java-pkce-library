package br.com.vanessacardoso.pkce;

import br.com.vanessacardoso.pkce.internal.CodeGenerator;
import br.com.vanessacardoso.pkce.internal.impl.CodeGeneratorPlain;
import br.com.vanessacardoso.pkce.internal.impl.CodeGeneratorS256;
import java.io.InputStream;
import java.util.Properties;

public class PKCEClient {
    private String codeVerifier;
    private final CodeGenerator codeGenerator;
    private final PKCEMethod pkceMethod;

    public PKCEClient() {
        this.pkceMethod = loadMethodFromProperties();
        if(this.pkceMethod == PKCEMethod.PLAIN){
            this.codeGenerator = new CodeGeneratorPlain();
        }
        else {
            this.codeGenerator = new CodeGeneratorS256();
        }
    }
    private PKCEMethod loadMethodFromProperties() {
        Properties prop = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("pkce.properties")) {
            if (input != null) {
                prop.load(input);
                String methodStr = prop.getProperty("pkce.method");
                return PKCEMethod.valueOf(methodStr.toUpperCase());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return PKCEMethod.S256;
    }
    public String generateAuthorizationUrl(String baseUrl, String clientId, String redirectUri){
        /*
        1. Geração e Armazenamento do codeVerifier
        2. Criação do codeChallenge (RF02)
        3. Montagem da URL (RF04)
        */
        baseUrl = baseUrl.replaceAll("/+$", "");
        redirectUri = redirectUri.replaceAll("/+$", "");
        this.codeVerifier = codeGenerator.generateCodeVerifier();
        String codeChallenge = codeGenerator.generateCodeChallenge(codeVerifier);
        return String.format("%s?response_type=code&client_id=%s&redirect_uri=%s" +
                "&code_challenge=%s&code_challenge_method=%s", baseUrl, clientId, redirectUri,codeChallenge, this.pkceMethod.getValue());
    }
    public String getCodeVerifier(){
        return this.codeVerifier;
    }
}
