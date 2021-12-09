package com.paymybuddy.service;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

@Service
public class Oauth2Service {

  /**
   * Return a Map with all informations relative to user and its Oauth2 registration provider.
   *
   * @param user the principal of user
   * @return oauth2 informations of user and registration name , null if don't retrieve them.
   */
  public Map<String, Object> getOauth2LoginInfo(Principal user) {

    Map<String, Object> Oauth2LoginInformation = new HashMap<String, Object>();

    OAuth2AuthenticationToken authenticateToken = ((OAuth2AuthenticationToken) user);

    Map<String, Object> oauth2Map =
        ((DefaultOAuth2User) authenticateToken.getPrincipal()).getAttributes();

    if (oauth2Map != null && authenticateToken.isAuthenticated()) {
      Oauth2LoginInformation.put(
          "ClientRegistrationId", authenticateToken.getAuthorizedClientRegistrationId());
      Oauth2LoginInformation.put("email", oauth2Map.get("email"));
      Oauth2LoginInformation.put("name", oauth2Map.get("name"));
      Oauth2LoginInformation.put("userId", oauth2Map.get("clientId"));
    }

    return Oauth2LoginInformation;
  }
}
