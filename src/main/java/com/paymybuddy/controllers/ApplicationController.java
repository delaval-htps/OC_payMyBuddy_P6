package com.paymybuddy.controllers;

import java.util.Optional;
import com.paymybuddy.dto.ApplicationAccountDto;
import com.paymybuddy.dto.ProfileUserDto;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Allow a authenticated user to go to home page. If he is not found , he is redirect to logout =
 * /loginPage (cf security configuration)
 */
@Controller
public class ApplicationController {

  @Autowired
  private UserService userService;

  @Autowired
  private ModelMapper modelMapper;

/**
 * Mapping for view home page.
 * @param authentication authentication for user
 * @param model model to return attributes for view 
 * @return either the home page if user authenticated or logout page if not
 */
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/home")
  public String getHome(Authentication authentication, Model model) {

    Optional<User> user = userService.findByEmail(authentication.getName());

    if (user.isPresent()) {
      User currentUser = user.get();
      ProfileUserDto profileUserDto = modelMapper.map(currentUser, ProfileUserDto.class);

      profileUserDto.setBankAccountRegistred(currentUser.getBankAccount() != null);
      profileUserDto.setBankCardRegistred(currentUser.getBankCard() != null);
      profileUserDto.setFullName(currentUser.getFullName());

      model.addAttribute("user", profileUserDto);
      model.addAttribute("applicationAccount", modelMapper.map(currentUser.getApplicationAccount(), ApplicationAccountDto.class));
      return "home";
    } else {
      authentication.setAuthenticated(false);
      return ("redirect:/logout");
    }
  }
}
