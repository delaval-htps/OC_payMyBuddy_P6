package com.paymybuddy.controllers;

import java.util.Optional;
import com.paymybuddy.dto.ApplicationAccountDto;
import com.paymybuddy.dto.UserDto;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {

  @Autowired
  private UserService userService;

  @Autowired
  private ModelMapper modelMapper;

  @GetMapping("/home")
  public String getHome(Authentication authentication, Model model) {
    Optional<User> user = userService.findByEmail(authentication.getName());

    if (user.isPresent() && authentication.isAuthenticated()) {
      User currentUser = user.get();
      UserDto userDto = modelMapper.map(currentUser, UserDto.class);

      userDto.setBankAccountRegistred(currentUser.getBankAccount() != null);
      userDto.setFullName(currentUser.getFullName());

      model.addAttribute("user", userDto);
      model.addAttribute("applicationAccount", modelMapper.map(currentUser.getApplicationAccount(), ApplicationAccountDto.class));
      return "home";
    } else {
      authentication.setAuthenticated(false);
      return ("redirect:/logout");
    }
  }
}
