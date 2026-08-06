package br.com.vanessacardoso.pkce;

import br.com.vanessacardoso.pkce.internal.impl.CodeGeneratorS256;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class PKCEClientTest {
    private PKCEClient client;
    private String baseUrl;
    private String clientId;
    private String redirectUri;
    private CodeGeneratorS256 codeGeneratorS256;
    @BeforeEach
    void setUp(){
        client = new PKCEClient();
        baseUrl = "https://server.com/authorize";
        clientId = "my-client-id";
        redirectUri = "https://app.com/callback";
        codeGeneratorS256 = new CodeGeneratorS256();
    }
    @Test
    void shouldntHaveCodeVerifier(){
        assertNull(client.getCodeVerifier());
    }
    @Test
    void shouldGenerateAuthorizationUrl(){
        String url = client.generateAuthorizationUrl(baseUrl,clientId,redirectUri);
        assertTrue(url.startsWith("https://server.com/authorize?"));
        assertTrue(url.contains("response_type=code"));
        assertTrue(url.contains("client_id=my-client-id"));
        assertTrue(url.contains("redirect_uri=https://app.com/callback"));
        assertTrue(url.contains("code_challenge="));
        assertTrue(url.contains("code_challenge_method="));
    }
    @Test
    void shouldStoreCodeVerifierAfterGeneratingUrl(){
        assertNull(client.getCodeVerifier());
        String url = client.generateAuthorizationUrl(baseUrl,clientId,redirectUri);
        String codeVerifier = client.getCodeVerifier();
        assertNotNull(codeVerifier);
        assertFalse(codeVerifier.isBlank());
    }
    //validacao tcc
    @Test
    void checkCodeChallengeAppendixB(){
        // code_verifier = dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
        // https://datatracker.ietf.org/doc/html/rfc7636#appendix-B
        // code_challenge = E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM
        // code_challenge_method=S256
        String codeVerifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
        String codeChallenge = codeGeneratorS256.generateCodeChallenge(codeVerifier);
        assertEquals(codeChallenge, "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM");
    }
    @Test
    void checkRF04CodeChallengeMethodAppendixB(){
        String url = client.generateAuthorizationUrl(baseUrl,clientId, redirectUri);
        assertTrue(url.contains("code_challenge="));
        assertTrue(url.contains("code_challenge_method=S256"));
    }
    @Test
    void checkRF03Base64URLSafe(){

    }
}
