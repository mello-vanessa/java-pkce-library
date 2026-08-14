package br.com.vanessacardoso.pkce.internal;

public interface HttpClientAdapter {
    public String postRequest(String url, String body, String headerName, String valueNam);
}
