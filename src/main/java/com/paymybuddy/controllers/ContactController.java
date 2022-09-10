package com.paymybuddy.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;

@Controller
public class ContactController {
    @Autowired
    private UserService userService;

    @GetMapping("/contact")
    public String getContactPage(Authentication authentication,Model model) {
        Optional<User> user = userService.findByEmail(authentication.getName());

        if (user.isPresent()) {
          
            return "contact";
        } else {
            return "redirect:/logout";
        }
        
    }
}
