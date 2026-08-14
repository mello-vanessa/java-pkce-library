package br.com.vanessacardoso.pkce.internal.impl;
import br.com.vanessacardoso.pkce.internal.HttpClientAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class JavaHttpClientAdapter implements HttpClientAdapter {
    @Override
    public String postRequest(String url, String body, String headerName, String valueName) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header(
                    headerName,
                    valueName
            ).POST(HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro HTTP: "+response.statusCode()+"-"+response.body());
            }
            System.out.println("PKCE VALIDADO COM SUCESSO!");
            return response.body();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
