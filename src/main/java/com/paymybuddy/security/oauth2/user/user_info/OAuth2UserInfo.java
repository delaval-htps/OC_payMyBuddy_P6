package com.paymybuddy.security.oauth2.user.user_info;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public abstract Long getClientId();

    public abstract String getEmail();

    public abstract String getFullName();

    public abstract String getLastName();

    public abstract String getFirstName();

    public abstract String getRegistrationId();

}
