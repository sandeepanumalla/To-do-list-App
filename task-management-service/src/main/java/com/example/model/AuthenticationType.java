package com.example.model;

public enum AuthenticationType {
    NORMAL("Normal"),
    OAUTH2("OAuth");

    private final String value;

    AuthenticationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
