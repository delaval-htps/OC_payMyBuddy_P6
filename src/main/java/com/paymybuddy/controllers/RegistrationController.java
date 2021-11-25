package com.paymybuddy.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class RegistrationController {

  @GetMapping("/registration")
  public String registerNewUser() {
    return "registration";
  }

}
