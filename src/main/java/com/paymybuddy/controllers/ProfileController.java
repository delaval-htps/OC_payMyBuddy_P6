package com.paymybuddy.controllers;

import java.util.Optional;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;
import com.paymybuddy.service.ApplicationAccountService;
import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationAccountService applicationAccountService;

    @GetMapping("/profile")
    public String getProfil(Authentication authentication, Model model) {
        Optional<User> user = userService.findByEmail(authentication.getName());

        if (user.isPresent()) {

            User currentUser = user.get();
            model.addAttribute("user", currentUser);
            Optional<ApplicationAccount> appAccount =
                    applicationAccountService.findByEmail(currentUser.getEmail());
            if (appAccount.isPresent()){
                model.addAttribute("appAccount", currentUser.getApplicationAccount());
            }
           
            BankAccount bankAccount = new BankAccount();
            model.addAttribute("bankAccount", bankAccount);
            
            BankCard bankCard = new BankCard();
            model.addAttribute("bankCard", bankCard);
        }
        return "profile";
    }
}
