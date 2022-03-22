package com.paymybuddy.controllers;

import java.util.Optional;
import javax.validation.Valid;
import com.paymybuddy.dto.ApplicationAccountDto;
import com.paymybuddy.dto.BankAccountDto;
import com.paymybuddy.dto.UserDto;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.service.BankAccountService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

public class ProfileController {
        @Autowired
        private UserService userService;

        @Autowired
        private BankAccountService bankAccountService;

        @Autowired
        private ModelMapper modelMapper;

        @GetMapping("/profile")
        public String getProfil(Authentication authentication, Model model,
                        RedirectAttributes redirectAttributes) {
                Optional<User> user = userService.findByEmail(authentication.getName());

                if (user.isPresent()) {

                        User currentUser = user.get();
                        UserDto userDto = modelMapper.map(currentUser, UserDto.class);
                        userDto.setFullName(currentUser.getFullName());
                        model.addAttribute("user", userDto);

                        model.addAttribute("applicationAccount",
                                        modelMapper.map(currentUser.getApplicationAccount(),
                                                        ApplicationAccountDto.class));

                        BankAccountDto bankAccountDto = currentUser.getBankAccount() != null
                                        ? modelMapper.map(currentUser.getBankAccount(),
                                                        BankAccountDto.class)
                                        : new BankAccountDto();
                        model.addAttribute("bankAccount", bankAccountDto);

                }
                return "profile";
        }

        @PostMapping("/profile/bankaccount")
        public String createBankAccount(Model model,
                        @Valid @ModelAttribute(value = "bankAccount") BankAccountDto bankAccountDto,
                        BindingResult bindingResult, RedirectAttributes redirectAttributes,
                        Authentication authentication) {

                Optional<User> user = userService.findByEmail(authentication.getName());

                if (user.isPresent()) {

                        User currentUser = user.get();

                        if (bindingResult.hasErrors()) {
                                model.addAttribute("user",
                                                modelMapper.map(currentUser, UserDto.class));
                                model.addAttribute("applicationAccount",
                                                modelMapper.map(currentUser.getApplicationAccount(),
                                                                ApplicationAccountDto.class));
                                
                                return "/profile";
                        }

                        BankAccount bankAccount =
                                        modelMapper.map(bankAccountDto, BankAccount.class);
                        currentUser.setBankAccount(bankAccount);

                        BankAccount userBankAccount = bankAccountService.save(bankAccount);

                        redirectAttributes.addFlashAttribute("success",
                                        "your bank account was correctly registred.");

                        UserDto userDto = modelMapper.map(currentUser, UserDto.class);
                        userDto.setFullName(currentUser.getFullName());

                        model.addAttribute("user", userDto);
                        model.addAttribute("applicationAccount",
                                        modelMapper.map(currentUser.getApplicationAccount(),
                                                        ApplicationAccountDto.class));
                        model.addAttribute("bankAccount",
                                        modelMapper.map(userBankAccount, BankAccountDto.class));

                        return "profile";

                } else {
                        throw new UserNotFoundException(
                                        "this user with email " + authentication.getName()
                                                        + " is not registred in application!");
                }

        }
}
