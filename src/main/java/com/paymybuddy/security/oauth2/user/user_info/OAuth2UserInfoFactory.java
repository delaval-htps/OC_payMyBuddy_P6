package com.paymybuddy.security.oauth2.user.user_info;

import java.util.Map;

import com.paymybuddy.model.AuthProvider;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfoService(String registrationId, Map<String, Object> attributes) {
        if (registrationId == null) {
            return null;
        }
        if (registrationId.equalsIgnoreCase(AuthProvider.GITHUB.toString())) {
            return new GithubUserInfo(attributes);
        }
        if (registrationId.equalsIgnoreCase(AuthProvider.GOOGLE.toString())) {
            return new GoogleUserInfo(attributes);
        }
        if (registrationId.equalsIgnoreCase(AuthProvider.FACEBOOK.toString())) {
            return new FacebookUserInfo(attributes);
        }
        return null;

    }
}
