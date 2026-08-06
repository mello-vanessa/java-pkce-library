package br.com.vanessacardoso.pkce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class PKCEClientTest {
    private PKCEClient client;
    private String authUrl;
    private String clientId;
    private String redirectUri;
    @BeforeEach
    void setUp(){
        client = new PKCEClient();
        authUrl = "https://server.com/authorize";
        clientId = "my-client-id";
        redirectUri = "https://app.com/callback";
    }
    @Test
    void shouldntHaveCodeVerifier(){
        assertNull(client.getCodeVerifier());
    }
    @Test
    void shouldGenerateAuthorizationUrl(){
        String url = client.generateAuthorizationUrl(authUrl,clientId,redirectUri);
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
        String url = client.generateAuthorizationUrl(authUrl,clientId,redirectUri);
        String codeVerifier = client.getCodeVerifier();
        assertNotNull(codeVerifier);
        assertFalse(codeVerifier.isBlank());
    }
    @Test
    void checkCodeVerifier(){

    }
}
