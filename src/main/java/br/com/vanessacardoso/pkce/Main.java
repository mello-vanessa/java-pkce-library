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
        PKCEClient client = new PKCEClient();
        String keycloakAuthUrl = "http://localhost:8080/realms/meu-realm/protocol/openid-connect/auth";
        String clientId = "minha-app-spa";
        String redirectUri = "http://localhost:3000/callback";

        String authUrl = client.generateAuthorizationUrl(keycloakAuthUrl, clientId, redirectUri);
        String codeVerifier = client.getCodeVerifier();

        System.out.println("=== Validação Experimental PKCE ===");
        System.out.println("Base URL: " + keycloakAuthUrl);
        System.out.println("Client ID: " + clientId);
        System.out.println("[RF01/RF05] Code Verifier Gerado: "+codeVerifier);
        System.out.println("[RF04] URL de Autorização Pronta para Redirecionamento:"+authUrl);
    }
}
