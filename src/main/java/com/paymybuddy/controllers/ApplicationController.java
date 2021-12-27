package com.paymybuddy.controllers;

import java.util.Map;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.paymybuddy.model.User;
import com.paymybuddy.service.Oauth2Service;
import com.paymybuddy.service.UserService;

@Controller
public class ApplicationController {

  @Autowired
  private Oauth2Service oauth2Service;
  @Autowired
  private UserService userService;

  @GetMapping("/home")
  @RolesAllowed("USER")
  public String getHome(Authentication authenticationUser, Model model) {
    if (authenticationUser.isAuthenticated()) {
      if (authenticationUser instanceof OAuth2AuthenticationToken) {
        Map<String, Object> oauth2LoginInfo = oauth2Service.getOauth2LoginInfo(authenticationUser);

        Optional<User> user = userService.findUserByOauth2Information(
            oauth2LoginInfo.get("name").toString(), oauth2LoginInfo.get("email").toString());

        if (user.isPresent()) {
          model.addAttribute("user", user.get());
          return "home";
        } else {
          model.addAttribute("Oauth2User", oauth2LoginInfo);
          return "registration";
        }
      } else if (authenticationUser instanceof UsernamePasswordAuthenticationToken) {
        model.addAttribute("user", userService.findByEmail(authenticationUser.getName()));
        return "home";
      }
    }
    return "login";
  }
}
