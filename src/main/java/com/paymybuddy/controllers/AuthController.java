package com.paymybuddy.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {


  @GetMapping(value = { "/", "/loginPage" })
  public String showLoginPage(Authentication authentication) {

    // mapping to not be able to return on loginPage if user is authenticated
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return "login";
    } else {
      return "redirect:/home";
    }
  }

}
