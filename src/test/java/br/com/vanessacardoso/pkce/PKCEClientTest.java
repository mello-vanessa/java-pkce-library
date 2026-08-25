package br.com.vanessacardoso.pkce;

import br.com.vanessacardoso.pkce.internal.Base64Encoder;
import br.com.vanessacardoso.pkce.internal.HttpClientAdapter;
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
    private String tokenUri;
    private String authorizationCode;
    private CodeGeneratorS256 codeGeneratorS256;
    @BeforeEach
    void setUp(){
        client = new PKCEClient();
        baseUrl = "https://server.com/authorize";
        clientId = "my-client-id";
        redirectUri = "https://app.com/callback";
        tokenUri = "https://server.com/token";
        authorizationCode = "my-auth-code";
        codeGeneratorS256 = new CodeGeneratorS256();
    }
    /*
    RF01 (Entropia): Verificar se o code_verifier gerado possui entre 43 e 128 caracteres e se utiliza apenas
    caracteres não reservados
    https://datatracker.ietf.org/doc/html/rfc7636#appendix-B
    code_verifier = dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk
    https://datatracker.ietf.org/doc/html/rfc7636#appendix-B
    code_challenge = E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM
    code_challenge_method=S256
     */
    @Test
    void checkRF01CodeVerifierAppendixB(){
        byte[] data = {
            (byte)116, (byte)24, (byte)223, (byte)180, (byte)151, (byte)153,
                    (byte)224, (byte)37, (byte)79, (byte)250, (byte)96, (byte)125,
                    (byte)216, (byte)173, (byte)187, (byte)186, (byte)22, (byte)212,
                    (byte)37, (byte)77, (byte)105, (byte)214, (byte)191, (byte)240,
                    (byte)91, (byte)88, (byte)5, (byte)88, (byte)83, (byte)132,
                    (byte)141, (byte)121
        };
        String codeVerifier = Base64Encoder.encodeUrlSafe(data);
        assertEquals(codeVerifier, "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk");
    }
    @Test
    void checkRF01CodeChallengeAppendixB(){
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
        client.generateAuthorizationUrl(baseUrl,clientId,redirectUri);
        String codeVerifier = client.getCodeVerifier();
        assertNotNull(codeVerifier);
        assertFalse(codeVerifier.isBlank());
    }
    @Test
    void shouldExchangeTokenSuccessfully(){
        HttpClientAdapter fakeHttpClient = new FakeHttpClientAdapter();
        PKCEClient pkceClientFake = new PKCEClient(fakeHttpClient);
        pkceClientFake.generateAuthorizationUrl(baseUrl, clientId, redirectUri);
        String token = pkceClientFake.getToken(authorizationCode,clientId,redirectUri,tokenUri);
        assertTrue(token.contains("fake-token"));
    }

    private static class FakeHttpClientAdapter implements HttpClientAdapter {
        @Override
        public String postRequest(String url, String body, String headerName, String headerValue) {
            return "{\"access_token\":\"fake-token\"}";
        }
    }
}
