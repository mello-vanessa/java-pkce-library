package br.com.vanessacardoso.pkce;

import br.com.vanessacardoso.pkce.internal.CodeGenerator;
import br.com.vanessacardoso.pkce.internal.HttpClientAdapter;
import br.com.vanessacardoso.pkce.internal.UrlEncoder;
import br.com.vanessacardoso.pkce.internal.impl.CodeGeneratorPlain;
import br.com.vanessacardoso.pkce.internal.impl.CodeGeneratorS256;
import br.com.vanessacardoso.pkce.internal.impl.JavaHttpClientAdapter;

import java.io.InputStream;
import java.util.Properties;

public class PKCEClient {
    private String codeVerifier;
    private final CodeGenerator codeGenerator;
    private final PKCEMethod pkceMethod;
    private final HttpClientAdapter httpClientAdapter;
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public PKCEClient() {
        this.pkceMethod = loadMethodFromProperties();
        if(this.pkceMethod == PKCEMethod.PLAIN){
            this.codeGenerator = new CodeGeneratorPlain();
        }
        else {
            this.codeGenerator = new CodeGeneratorS256();
        }
        this.httpClientAdapter = new JavaHttpClientAdapter();
    }
    public PKCEClient(HttpClientAdapter httpClientAdapter) {
        this.pkceMethod = loadMethodFromProperties();

        if (this.pkceMethod == PKCEMethod.PLAIN) {
            this.codeGenerator = new CodeGeneratorPlain();
        } else {
            this.codeGenerator = new CodeGeneratorS256();
        }

        this.httpClientAdapter = httpClientAdapter;
    }
    private PKCEMethod loadMethodFromProperties() {
        Properties prop = new Properties();
        ClassLoader classLoader =  Thread.currentThread().getContextClassLoader();

        if (classLoader == null){
            classLoader = getClass().getClassLoader();
        }

        try (InputStream input = classLoader.getResourceAsStream("pkce.properties")) {
            if (input != null) {
                prop.load(input);
                String methodStr = prop.getProperty("pkce.method");
                if(methodStr != null && !methodStr.trim().isEmpty()){
                    System.out.println("[PKCE] Método: " + PKCEMethod.valueOf(methodStr.toUpperCase()));
                    return PKCEMethod.valueOf(methodStr.toUpperCase());
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[PKCE] Método inválido. Utilizando S256.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return PKCEMethod.S256;
    }

    public String generateAuthorizationUrl(String baseUrl, String clientId, String redirectUri){
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

    public String generateTokenRequestPayload(String authorizationCode, String clientId, String redirectUri){
        if (this.codeVerifier == null) {
            throw new IllegalStateException("O code_verifier ainda não foi gerado. Chame generateAuthorizationUrl primeiro.");
        }
        return String.format("grant_type=authorization_code&code=%s&redirect_uri=%s&client_id=%s&code_verifier=%s",
                UrlEncoder.encodeUrl(authorizationCode), UrlEncoder.encodeUrl(redirectUri), UrlEncoder.encodeUrl(clientId), UrlEncoder.encodeUrl(this.codeVerifier));
    }

    public String getToken(String authorizationCode, String clientId, String redirectUri, String tokenEndpoint){
        String tokenPayload = this.generateTokenRequestPayload(authorizationCode, clientId, redirectUri);
        return httpClientAdapter.postRequest(tokenEndpoint,tokenPayload,HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED);
    }
}
