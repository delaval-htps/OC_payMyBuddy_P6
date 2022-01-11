package com.paymybuddy.security.services;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

@Service
public class GithubUserInfoServiceImpl implements OAuth2UserInfoService {

  /**
   * Return a Map with all informations relative to user and its Oauth2
   * registration provider.
   *
   * @param user the principal of user
   * @return oauth2 informations of user and registration name , null if don't
   *         retrieve them.
   */
  @Override
  public Map<String, Object> getOauth2LoginInfo(Principal user) {

    Map<String, Object> oAuth2LoginInformation = new HashMap<>();

    OAuth2AuthenticationToken authenticateToken = ((OAuth2AuthenticationToken) user);

    Map<String, Object> oAuth2Map = ((DefaultOAuth2User) authenticateToken.getPrincipal()).getAttributes();
    String authorizedClientRegistrationId = authenticateToken.getAuthorizedClientRegistrationId();

    if (oAuth2Map != null) {
      oAuth2LoginInformation.put(
          "ClientRegistrationId", authenticateToken.getAuthorizedClientRegistrationId());
      oAuth2LoginInformation.put("email", oAuth2Map.get("email"));
      oAuth2LoginInformation.put("name", oAuth2Map.get("name"));
      oAuth2LoginInformation.put("userId", oAuth2Map.get("clientId"));
      oAuth2LoginInformation.put("clientRegistrationId", authorizedClientRegistrationId);
    }

    return oAuth2LoginInformation;
  }
}
