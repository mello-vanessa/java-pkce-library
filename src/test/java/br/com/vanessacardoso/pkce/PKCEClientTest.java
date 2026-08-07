package br.com.vanessacardoso.pkce;

import br.com.vanessacardoso.pkce.internal.Base64Encoder;
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
    /*
    RF01 (Entropia): Verificar se o code_verifier gerado possui entre 43 e 128 caracteres e se utiliza apenas
    caracteres não reservados
     */
    @Test
    void checkRF01CodeChallengeAppendixB(){
        // code_verifier = dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
        // https://datatracker.ietf.org/doc/html/rfc7636#appendix-B
        // code_challenge = E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM
        // code_challenge_method=S256
        String codeVerifier = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
        String codeChallenge = codeGeneratorS256.generateCodeChallenge(codeVerifier);
        assertEquals(codeChallenge, "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM");
    }
    /*
    RF02: Validar se o resultado do hash não possui preenchimento (padding com =) e se os caracteres
    + e / foram substituídos por - e _, respectivamente
     */
    @Test
    void checkRF02CodeChallengeMethodAppendixB(){
        String url = client.generateAuthorizationUrl(baseUrl,clientId, redirectUri);
        assertTrue(url.contains("code_challenge="));
        assertTrue(url.contains("code_challenge_method=S256"));
    }
    /*
    RF03: Codificação Base64 URL-Safe: o resultado do hash será codificado no formato Base64 URL-Safe, sem
    preenchimento (padding), atendendo rigorosamente à norma técnica da RFC7636
     */
    @Test
    void checkRF03Base64URLSafeAppendixA(){
        byte[] data = {(byte)3, (byte)236, (byte)255, (byte)224, (byte)193};
        String encoded = Base64Encoder.encodeUrlSafe(data);
        assertEquals("A-z_4ME", encoded);
    }
    /*
    RF04 (Montagem da URL): Validar se a URL gerada pelo PKCEClient contém os parâmetros code_challenge e
    code_challenge_method formatados corretamente para o Keycloak
     */
    @Test
    void checkRF04GenerateAuthorizationUrl(){
        String url = client.generateAuthorizationUrl(baseUrl,clientId,redirectUri);
        assertTrue(url.startsWith("https://server.com/authorize?"));
        assertTrue(url.contains("response_type=code"));
        assertTrue(url.contains("client_id=my-client-id"));
        assertTrue(url.contains("redirect_uri=https://app.com/callback"));
        assertTrue(url.contains("code_challenge="));
        assertTrue(url.contains("code_challenge_method="));
    }
    /*
    RF05 (Gestão de Estado): Garantir que o code_verifier não seja null no momento da troca do token.
    Validar a ordem de execução (gerar a URL antes de pedir o verifier).
     */
    @Test
    void shouldStoreCodeVerifierAfterGeneratingUrl(){
        assertNull(client.getCodeVerifier());
        String url = client.generateAuthorizationUrl(baseUrl,clientId,redirectUri);
        String codeVerifier = client.getCodeVerifier();
        assertNotNull(codeVerifier);
        assertFalse(codeVerifier.isBlank());
    }
}
