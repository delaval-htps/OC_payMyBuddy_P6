package com.paymybuddy.controllers;

import java.util.Optional;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {

  @Autowired
  private UserService userService;

  @GetMapping("/home")
  public String getHome(Authentication authentication, Model model) {
    Optional<User> user = userService.findByEmail(authentication.getName());

    if (user.isPresent() && authentication.isAuthenticated()) {
      model.addAttribute("user", user.get());
      return "home";
    } else {
      authentication.setAuthenticated(false);
      return ("redirect:/logout");
    }
  }

 

}
