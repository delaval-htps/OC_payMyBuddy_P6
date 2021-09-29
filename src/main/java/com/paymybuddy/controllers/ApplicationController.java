package com.paymybuddy.controllers;

import javax.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {

  @RolesAllowed("USER")
  @GetMapping("/")
  public String getHome() {
    return "home";

  }
}
