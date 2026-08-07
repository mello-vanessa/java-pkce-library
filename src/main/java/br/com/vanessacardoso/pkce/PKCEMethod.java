package br.com.vanessacardoso.pkce;

public enum PKCEMethod {
    S256("S256"),
    PLAIN("plain");
    private final String value;

    PKCEMethod(String value) {
        this.value = value;
    }
    public String getValue(){
        return this.value;
    }
}
