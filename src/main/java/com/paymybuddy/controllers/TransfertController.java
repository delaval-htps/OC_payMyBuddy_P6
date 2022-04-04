package com.paymybuddy.controllers;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import com.paymybuddy.dto.ConnectedUsersDto;
import com.paymybuddy.dto.TransactionDto;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
import org.modelmapper.ModelMapper;
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
public class TransfertController {

  @Autowired
  private UserService userService;

  @Autowired
  private ModelMapper modelMapper;


  @GetMapping("/transfert")
  public String getTransfert(Authentication authentication, Model model) {
    Optional<User> user = userService.findByEmail(authentication.getName());

    if (user.isPresent()) {

      User existedUser = user.get();
      List<User> connectedUsers = userService.findConnectedUserByEmail(existedUser.getEmail());

      ConnectedUsersDto connectedUsersDto = new ConnectedUsersDto();

      for (User connectedUser : connectedUsers) {
        connectedUsersDto.getEmails().add(connectedUser.getEmail());
      }

      TransactionDto transactionDto = new TransactionDto();
      model.addAttribute("transaction", transactionDto);
      model.addAttribute("connectedUsers", connectedUsersDto);
      return "transfert";
    } else {
      throw new UserNotFoundException("this user is not authenticated!");
    }
  }


  @PostMapping("/connection")
  public String saveConnectionUser(@Valid String email, RedirectAttributes redirectAttrs, Authentication auth) {

    Optional<User> existedUser = userService.findByEmail(email);
    Optional<User> authenticatedUser = userService.findByEmail(auth.getName());

    if (authenticatedUser.isPresent()) {
      User user = authenticatedUser.get();

      if (existedUser.isPresent()) {

        User connectionUser = existedUser.get();
        List<User> connectedUsers = userService.findConnectedUserByEmail(user.getEmail());

        if (connectedUsers.contains(connectionUser)) {
          redirectAttrs.addFlashAttribute("warning", "the user with this email " + email + " already connected with you!");
        } else {
          user.addConnectionUser(connectionUser);
          userService.save(user);
          redirectAttrs.addFlashAttribute("success", "the user with this email " + email + " was registred!");
        }
      } else {

        log.error("Not be able to add connectionUser with email: {} cause of not found in database.", email);
        redirectAttrs.addFlashAttribute("error", "the user with this email " + email + " is not registred in application!");
      }
    } else {

      throw new UserNotFoundException("the user is not authenticated.");
    }
    return "redirect:/transfert";
  }
}
