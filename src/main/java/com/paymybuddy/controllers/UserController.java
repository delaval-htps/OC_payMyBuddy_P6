package com.paymybuddy.controllers;

import com.paymybuddy.security.services.SecurityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

  @Autowired
  private SecurityService securityService;

  @GetMapping("/showLoginPage")
  public String showLoginPage() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
      return "login";
    } else {
      return "redirect:/home";
    }
  }

  @PostMapping("/authenticateUser")
  public String login(String username, String password) {
    boolean responseLogin = securityService.login(username, password);
    if (responseLogin) {
      return "home";
    }
    return "login";
  }
}
