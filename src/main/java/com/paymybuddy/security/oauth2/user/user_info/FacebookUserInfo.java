package com.paymybuddy.security.oauth2.user.user_info;

import java.util.Map;

import com.paymybuddy.model.AuthProvider;

public class FacebookUserInfo extends OAuth2UserInfo {

  public FacebookUserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getClientId() {
    return attributes.get("id").toString();
  }

  @Override
  public String getEmail() {
    return attributes.get("email").toString();
  }

  @Override
  public String getRegistrationId() {
    return AuthProvider.FACEBOOK.toString();
  }

  @Override
  public String getFullName() {
    return attributes.get("name").toString();
  }

  @Override
  public String getLastName() {
    String fullName = this.getFullName();
    return fullName.split(" ")[1];
  }

  @Override
  public String getFirstName() {
    String fullName = this.getFullName();
    return fullName.split(" ")[0];
  }

  @Override
  public String toString() {
    return this.getClientId() + "\t:" + this.getEmail() + "\t:" + this.getRegistrationId() + "\t:" + this.getFullName()
        + "\t:" + this.getLastName() + "\t:" + this.getFirstName();
  }
}
