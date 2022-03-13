package com.paymybuddy.controllers;

import java.util.Optional;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
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
    
    @GetMapping("/profile")
    public String getProfil(Authentication authentication,Model model){
        Optional<User> user = userService.findByEmail(authentication.getName());
        
        if (user.isPresent()){

            User currentUser = user.get();
            model.addAttribute("user", currentUser);
            BankAccount bankAccount = new BankAccount();
            model.addAttribute("bankAccount", bankAccount);
        }
        return "profile";
    }
}
