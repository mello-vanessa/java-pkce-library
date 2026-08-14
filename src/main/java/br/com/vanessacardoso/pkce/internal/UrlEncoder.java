package br.com.vanessacardoso.pkce.internal;

import java.nio.charset.StandardCharsets;

public class UrlEncoder {
    public static String encodeUrl(String value){
        try {
            return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
