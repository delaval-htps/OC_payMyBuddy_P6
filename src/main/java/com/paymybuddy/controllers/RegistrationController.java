package com.paymybuddy.controllers;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.paymybuddy.dto.UserDto;
import com.paymybuddy.exceptions.UserException;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class RegistrationController {

  @Autowired private UserService userService;

  @GetMapping("/registration")
  public String registerNewUser(Model model) {

    UserDto userDto = new UserDto();
    model.addAttribute("user", userDto);
    return "registration";
  }

  @PostMapping("/registration")
  public String saveNewUser(Model model, @RequestBody @Valid UserDto userDto) {

    User newUser = new User();

    if (userDto != null) {
      newUser.setLastName(userDto.getLastName());
      newUser.setFirstName(userDto.getLastName());
      newUser.setAddress(userDto.getAddress());
      newUser.setAddress(userDto.getAddress());
      newUser.setPhone(userDto.getPhone());
      newUser.setZip(userDto.getZip());
      newUser.setEmail(userDto.getEmail());
      newUser.setPassword(userDto.getPassword());
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
