package com.vineeth.bankservice.security;

public class AuthorizePayload {
    private String username;
    private String scopes;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getScopes() {
        return scopes;
    }

    public void setScope(String scopes) {
        this.scopes = scopes;
    }
}
