package com.paymybuddy.security.oauth2.user.user_info;

import java.util.Map;

import com.paymybuddy.model.AuthProvider;

public class GoogleUserInfo extends OAuth2UserInfo {

  public GoogleUserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getClientId() {
    return attributes.get("sub").toString();
  }

  @Override
  public String getEmail() {
    return attributes.get("email").toString();
  }

  @Override
  public String getRegistrationId() {
    return AuthProvider.GOOGLE.toString();
  }

  @Override
  public String getFullName() {
    return attributes.get("name").toString();
  }

  @Override
  public String getLastName() {
    return attributes.get("family_name").toString();
  }

  @Override
  public String getFirstName() {
    return attributes.get("given_name").toString();
  }

  @Override
  public String toString() {
    return this.getClientId() + "\t:" + this.getEmail() + "\t:" + this.getRegistrationId() + "\t:" + this.getFullName()
        + "\t:" + this.getLastName() + "\t:" + this.getFirstName();
  }
}
