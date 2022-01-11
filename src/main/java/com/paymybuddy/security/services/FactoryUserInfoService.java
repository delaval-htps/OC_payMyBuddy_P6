package com.paymybuddy.security.services;

public class FactoryUserInfoService {

    public OAuth2UserInfoService getAuth2UserInfoService(String clientProviderString) {
        if (clientProviderString == null) {
            return null;
        }
        if (clientProviderString.equalsIgnoreCase("github")) {
            return new GithubUserInfoServiceImpl();
        }

        return null;

    }
}
