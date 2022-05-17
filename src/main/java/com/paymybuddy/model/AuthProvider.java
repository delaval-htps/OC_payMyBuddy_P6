package com.paymybuddy.model;

/**
 * enum of Oauth2Provider autorised for the application.
 * 
 */
public enum AuthProvider {

    LOCAL("local"), GOOGLE("google"), GITHUB("github"), FACEBOOK("facebook");

    private String name;

    AuthProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * retrieve the name (enum string) by a string name given in parameter.
     */
    public static AuthProvider fromString(String name) {
        for (AuthProvider provider : AuthProvider.values()) {
            if (provider.name.equalsIgnoreCase(name)) {
                return provider;
            }
        }
        return null;
    }
}
