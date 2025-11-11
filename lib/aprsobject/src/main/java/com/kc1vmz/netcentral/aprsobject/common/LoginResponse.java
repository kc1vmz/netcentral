package com.kc1vmz.netcentral.aprsobject.common;

public class LoginResponse {
    private String id;
    private String emailAddress;
    private String accessToken;
    private String role;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

}
