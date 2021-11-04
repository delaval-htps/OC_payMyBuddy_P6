package com.paymybuddy.controllers;

import java.security.Principal;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {


  private final OAuth2AuthorizedClientService authorizedClientService;

  public ApplicationController(OAuth2AuthorizedClientService authorizedClientService) {
    this.authorizedClientService = authorizedClientService;
  }

  @GetMapping("/")
  @RolesAllowed("USER")
  public String getHome(Principal user, Model model) {

    if (user instanceof UsernamePasswordAuthenticationToken) {
      model.addAttribute("userName", getUserNamePasswordLoginInfo(user));
    } else if (user instanceof OAuth2AuthenticationToken) {
      model.addAttribute("Oauth2Info", getOauth2LoginInfo(user));
    }
    return "home";

  }

  private StringBuilder getUserNamePasswordLoginInfo(Principal user) {
    StringBuilder userNameInfo = new StringBuilder();

    UsernamePasswordAuthenticationToken token = ((UsernamePasswordAuthenticationToken) user);

    if (token.isAuthenticated()) {
      User u = (User) token.getPrincipal();
      userNameInfo.append(u.getUsername());
    } else {
      userNameInfo.append("NA");
    }
    return userNameInfo;
  }

  private StringBuilder getOauth2LoginInfo(Principal user) {

    StringBuilder protectedInfo = new StringBuilder();

    OAuth2AuthenticationToken authenticateToken = ((OAuth2AuthenticationToken) user);

    OAuth2User principal = ((OAuth2AuthenticationToken) user).getPrincipal();

    OidcIdToken idToken = getIdToken(principal);

    Map<String, Object> userDetails = ((DefaultOAuth2User) authenticateToken.getPrincipal())
        .getAttributes();

    protectedInfo.append("\n" + userDetails.get("name") + " \n");

    if (idToken != null) {
      protectedInfo.append(idToken.getEmail() + "\n");
    } else {
      protectedInfo.append("le id token est null " + "\n");
    }
    protectedInfo.append(authenticateToken.getName() + "\n");
    protectedInfo.append(authenticateToken.getAuthorizedClientRegistrationId());


    return protectedInfo;

  }

  private OidcIdToken getIdToken(OAuth2User principal) {

    if (principal instanceof DefaultOidcUser) {
      DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
      return oidcUser.getIdToken();
    }
    return null;
  }
}
