package com.paymybuddy.controllers;

import javax.annotation.security.RolesAllowed;

import com.paymybuddy.security.services.GithubUserInfoServiceImpl;
import com.paymybuddy.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {

  @GetMapping("/")
  @RolesAllowed("USER")
  public String getHome(Authentication authenticationUser, Model model) {
    return "home";
  }
}
