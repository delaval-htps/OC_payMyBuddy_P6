package com.paymybuddy.controllers;

import javax.validation.Valid;
import com.paymybuddy.dto.UserDto;
import com.paymybuddy.exceptions.UserException;
import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class RegistrationController {

  @Autowired
  private UserService userService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @GetMapping("/registration")
  public String registerNewUser(Model model,
      org.springframework.security.core.Authentication authentication) {

    UserDto userDto = new UserDto();

    if (authentication != null) {
      CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
      userDto.setEmail(oAuth2User.getEmail());
      userDto.setLastName(oAuth2User.getLastName());
      userDto.setFirstName(oAuth2User.getFirstName());

    }
    model.addAttribute("user", userDto);
    return "registration";
  }

  @PostMapping("/registration")
  public String saveNewUser(Model model, @Valid @ModelAttribute(value ="user") UserDto userDto,BindingResult bindingResult) {
    
    if (bindingResult.hasErrors()){
      System.out.println(bindingResult.toString());
            return "registration";
    }

    User newUser = new User();

    if (userDto != null) {
      if (userDto.getLastName() != null && !userDto.getLastName().trim().equalsIgnoreCase("")) {
        newUser.setLastName(userDto.getLastName());
      }
      if (userDto.getFirstName() != null && !userDto.getFirstName().trim().equalsIgnoreCase("")) {
        newUser.setFirstName(userDto.getFirstName());
      }
      if (userDto.getAddress() != null && !userDto.getAddress().trim().equalsIgnoreCase("")) {
        newUser.setAddress(userDto.getAddress());
      }
      newUser.setPhone(userDto.getPhone());
      newUser.setZip(userDto.getZip());
      newUser.setCity(userDto.getCity());
      newUser.setEmail(userDto.getEmail());
      newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
      newUser.setEnabled((byte) 1);

      userService.saveUser(newUser);
    } else {
      log.error("Error to registre the new user.");
      throw new UserException("Error to registre the new user.");
    }
    model.addAttribute("user", newUser);
    return "home";
  }
}
