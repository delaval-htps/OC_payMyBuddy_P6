package com.paymybuddy.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transaction;
import javax.validation.Valid;
import com.paymybuddy.dto.ConnectedUserDto;
import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.User;
import com.paymybuddy.service.ApplicationTransactionService;
import com.paymybuddy.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/transfert")
@Log4j2
public class TransfertController {

  @Autowired
  private UserService userService;

  @Autowired
  private ApplicationTransactionService appTransactionService;

  @Autowired
  private ModelMapper modelMapper;


  @GetMapping("/")
  public String getTransfert(Authentication authentication, Model model) {
    Optional<User> user = userService.findByEmail(authentication.getName());

    if (user.isPresent()) {

      User existedUser = user.get();
      List<User> connectedUsers = userService.findConnectedUserByEmail(existedUser.getEmail());

      List<ConnectedUserDto> connectedUsersDto = new ArrayList<>();

      for (User connectedUser : connectedUsers) {
        connectedUsersDto.add(modelMapper.map(connectedUser, ConnectedUserDto.class));
      }

      ApplicationTransactionDto transactionDto = new ApplicationTransactionDto();
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

  @PostMapping("/sendmoneyto")
  public String sendMoneyTo(@Valid @ModelAttribute(value = "transaction") ApplicationTransactionDto transactionDto, BindingResult bindingResult, Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
    Optional<User> user = userService.findByEmail(authentication.getName());

    if (bindingResult.hasErrors()) {
      model.addAttribute("transaction", transactionDto);
      return "/transfert";
    }

    if (user.isPresent()) {

      User sender = user.get();

      if (transactionDto != null) {

        ApplicationTransaction transaction = modelMapper.map(transactionDto, ApplicationTransaction.class);
        Optional<User> receiverUser = userService.findByEmail(transactionDto.getConnectionUserEmail());

        if (receiverUser.isPresent()) {

          User receiver = receiverUser.get();

          // save of transaction
          transaction.setTransactionDate(new Date());
          transaction.setConnectionUserId(receiver.getId());
          transaction.setAmountCommission(appTransactionService.calculateAmountCommission(transaction.getAmount()));
          transaction.setUserId(sender.getId());
          transaction.setConnectionUserId(receiver.getId());

          ApplicationTransaction succeededTransaction = appTransactionService.save(transaction);

          // update Amount of ApplicationAccount of user
          appTransactionService.updateUserApplicationAccountFollowingTransaction(succeededTransaction.getAmount(), succeededTransaction.getAmountCommission(), sender.getApplicationAccount());
          // update list of transactions for the user

          List<ApplicationTransaction> userAppTransactions = appTransactionService.findByUserId(sender.getId());

          model.addAttribute("userTransactions", userAppTransactions);
          redirectAttributes.addFlashAttribute("success", "Transaction of " + succeededTransaction.getAmount() + "€ " + "to " + receiver.getFullName() + " was successfull!");


        } else {
          log.error("Not be able to find connectionUser with email: {} during transaction cause of not found in database.", transactionDto.getConnectionUserEmail());
          redirectAttributes.addFlashAttribute("error", "the receiver doesn't not exist!");
        }
      } else {
        // throw new ApplicationTransactionException("there is a probleme with your transaction");
      }

    } else {
      throw new UserNotFoundException("this user is not authenticated");
    }

    return ("redirect:/transfert");
  }
}
