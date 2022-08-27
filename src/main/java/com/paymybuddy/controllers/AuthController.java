package com.paymybuddy.controllers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for authentication with login page.
 */
@Controller
public class AuthController {

/**
 * mehtod to avoid for user to return to login page by typing in url /loginPage if he's already on page of application.
 * @param authentication authentication of connected user
 * @return either login page if user not connected or home page if he is 
 */
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
