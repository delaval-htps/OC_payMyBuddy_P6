package com.paymybuddy.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {

  @GetMapping("/home")

  public String getHome(Authentication authentication, Model model) {
    String username = authentication.getName();
    model.addAttribute("userName", username);
   
    if (authentication.isAuthenticated()) {
      return "home";
    } else {
      authentication.setAuthenticated(false);
      return ("redirect:/logout");
    }
  }
}
