package com.paymybuddy.controllers;

import java.security.Principal;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.paymybuddy.service.ApplicationSecurityService;
import com.paymybuddy.service.UserService;

@Controller
public class ApplicationController {

  @Autowired
  private ApplicationSecurityService applicationSecurityService;
  private UserService userService;

  @GetMapping("/")
  @RolesAllowed("USER")
  public String getHome(Principal user, Model model) {
    String userName = null;
    String email = null;
    String firstName = null;
    String lastName = null;

    if (user instanceof UsernamePasswordAuthenticationToken) {
      userName = applicationSecurityService.getUserNameLoginPasswordInfo(user).toString();
      if (userName != null) {
        model.addAttribute("userName", userName);
        return "home";
      } else {
        return "myLoginPage?error";
        // throw new SecurityException("l'utilisateur n'existe pas!");
      }
    } else if (user instanceof OAuth2AuthenticationToken) {

      Map<String, Object> oauth2Information = applicationSecurityService.getOauth2LoginInfo(user);

      if (oauth2Information != null) {
        userName = oauth2Information.get("userName").toString().trim();
        email = oauth2Information.get("email").toString();
        firstName = null;
        lastName = null;

        if (userName != null && !userName.equalsIgnoreCase("") && email != null && !email.trim()
            .equalsIgnoreCase("")) {

          firstName = userName.split(" ")[0];
          lastName = userName.split(" ")[1];

          if (userService.findByUserOauth2Information(firstName, lastName, email).isPresent()) {
            model.addAttribute("userName", userName);
          } else {
            model.addAttribute("userName", userName);
            return "registration";
          }
        } else {
          return "myLoginPage?error";
        }
      }
    }
    return "home";
  }



}
