package com.paymybuddy.model;

public enum AuthProvider {
    LOCAL("local"),
    GOOGLE("google"),
    GITHUB("github"),
    FACEBOOK("facebook");

    private String name;

    AuthProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static AuthProvider fromString(String name) {
        for (AuthProvider provider : AuthProvider.values()) {
            if (provider.name.equalsIgnoreCase(name)) {
                return provider;
            }
        }
        return null;
    }
}
