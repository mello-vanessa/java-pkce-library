package br.com.vanessacardoso.pkce.internal;

import java.util.Base64;

public class Base64Encoder {
    public static String encodeUrlSafe(byte[] data){
        return Base64.getUrlEncoder().withoutPadding().encodeToString(data);
    }
}
