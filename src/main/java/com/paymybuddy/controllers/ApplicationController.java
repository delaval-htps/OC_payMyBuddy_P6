package com.paymybuddy.controllers;

import java.util.Optional;
import javax.validation.Valid;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class ApplicationController {

  @Autowired
  private UserService userService;

  @GetMapping("/home")
  public String getHome(Authentication authentication, Model model) {
    Optional<User> user= userService.findByEmail(authentication.getName());
   
    if (user.isPresent() && authentication.isAuthenticated()) {
      model.addAttribute("user", user.get());
      return "home";
    } else {
      authentication.setAuthenticated(false);
      return ("redirect:/logout");
    }
  }

  @GetMapping("/transfert")
  public String getTransfert(Authentication authentication, Model model) {
    if (authentication.isAuthenticated()) {
      return "transfert";
    } else {
      return ("redirect:/logout");
    }
  }

  @PostMapping("/connection")
  public String saveConnectionUser(@Valid String email,RedirectAttributes rAttributes, Authentication auth)
      throws AuthenticationException {

    Optional<User> existedUser = userService.findByEmail(email);
    Optional<User> authenticatedUser = userService.findByEmail(auth.getName());

    if (authenticatedUser.isPresent()) {

      User user = authenticatedUser.get();

      if (existedUser.isPresent()) {
        User connectionUser = existedUser.get();

        user.addConnectionUser(connectionUser);
        userService.save(user);

      } else {

        log.error(
            "Not be able to add connectionUser with email: {} cause of not found in database.",
            email);
        rAttributes.addFlashAttribute("error", "the user with this email is not registred in application!");
        return"redirect:/transfert/";
      }
    } else {

      throw new AuthenticationException("the user is not authenticated.");
    }
    return "transfert";
  }

}
