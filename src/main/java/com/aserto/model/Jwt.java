package com.aserto.model;

public class Jwt {
    private String sub;
    private String email;

    public Jwt() {
    }

    public Jwt(String key, String email) {
        this.sub = key;
        this.email = email;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
