package com.paymybuddy.controllers;

import java.util.Map;
import java.util.Optional;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.paymybuddy.model.User;
import com.paymybuddy.security.services.OAuth2ServiceImpl;
import com.paymybuddy.service.UserService;

@Controller
public class ApplicationController {

  @Autowired
  private OAuth2ServiceImpl oauth2Service;
  @Autowired
  private UserService userService;

  @GetMapping("/")
  @RolesAllowed("USER")
  public String getHome(Authentication authenticationUser, Model model) {
    return "home";
  }
}
