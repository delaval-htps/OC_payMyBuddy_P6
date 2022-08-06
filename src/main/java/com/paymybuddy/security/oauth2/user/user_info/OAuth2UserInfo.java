package com.paymybuddy.security.oauth2.user.user_info;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Abstract class that extends {@link GithubUserInfo} {@link FacebookUserInfo} and
 * {@link GoogleUserInfo}. to retrieve information about Oauth2User connected. In futur, we can use
 * it for another registrationId by just implement a another "registrationIdUserInfo". All methods
 * are overide in extended classes.
 */
@Getter
@AllArgsConstructor
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public abstract String getClientId();

    public abstract String getEmail();

    public abstract String getFullName();

    public abstract String getLastName();

    public abstract String getFirstName();

    public abstract String getRegistrationId();

}
