package com.paymybuddy.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

@Service
public class ApplicationSecurityService {

  /**
   * Return username if is authenticate with login/password.
   * 
   * @param user the principal of user
   * @return the username of user if is authenticated else return null
   */
  public StringBuilder getUserNameLoginPasswordInfo(Principal user) {
    StringBuilder userNameInfo = new StringBuilder();

    UsernamePasswordAuthenticationToken token = ((UsernamePasswordAuthenticationToken) user);

    if (token.isAuthenticated()) {
      User u = (User) token.getPrincipal();
      userNameInfo.append(u.getUsername());
    }
    return userNameInfo;
  }

  /**
   * Return a Map with all informations relative to user and its Oauth2 registration provider.
   * 
   * @param user the principal of user
   * @return informations of user and registration name , null if don't retrieve them.
   */
  public Map<String, Object> getOauth2LoginInfo(Principal user) {

    Map<String, Object> Oauth2LoginInformation = new HashMap<String, Object>();

    OAuth2AuthenticationToken authenticateToken = ((OAuth2AuthenticationToken) user);

    Map<String, Object> oauth2Map = ((DefaultOAuth2User) authenticateToken.getPrincipal())
        .getAttributes();

    if (oauth2Map != null) {
      Oauth2LoginInformation.put("ClientRegistrationId", authenticateToken
          .getAuthorizedClientRegistrationId());
      Oauth2LoginInformation.put("email", oauth2Map.get("email"));
      Oauth2LoginInformation.put("userName", oauth2Map.get("name"));
      Oauth2LoginInformation.put("userId", oauth2Map.get("name"));
    }

    return Oauth2LoginInformation;
  }


}
