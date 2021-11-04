package com.paymybuddy.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

  @GetMapping("/myLoginPage")
  public String showLoginPage() {

    return "login";
  }



}
