package com.paymybuddy.controllers;

import javax.annotation.security.RolesAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {

  @GetMapping("/home")
  @RolesAllowed("USER")
  public String getHome(Authentication authentication, Model model) {

    String username =authentication.getName();
    model.addAttribute("userName", username);   
    if (!(authentication instanceof OAuth2AuthenticationToken)){ 
      return "home";
    }else{
      authentication.setAuthenticated(false);
      return ("redirect:/logout");
    }
    
    
  }
}
