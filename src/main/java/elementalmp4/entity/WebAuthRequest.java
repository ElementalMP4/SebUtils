package main.java.elementalmp4.entity;

import main.java.elementalmp4.SebUtils;

public class WebAuthRequest extends EphemeralObject {

    private final String username;
    private final String authCode;

    public WebAuthRequest(String username, String authCode) {
        super(300000);
        this.username = username;
        this.authCode = authCode;
    }

    @Override
    public void whenExpired() {
        SebUtils.getPluginLogger().info("Auth request for " + username + " has expired");
    }

    public String getUsername() {
        return username;
    }

    public String getAuthCode() {
        return authCode;
    }
}
