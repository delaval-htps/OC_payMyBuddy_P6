package com.paymybuddy.controllers;

import java.util.Optional;
import javax.validation.Valid;
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
import com.paymybuddy.dto.ApplicationAccountDto;
import com.paymybuddy.dto.BankAccountDto;
import com.paymybuddy.dto.BankCardDto;
import com.paymybuddy.dto.UserDto;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.UserService;

@Controller

public class ProfileController {
        @Autowired
        private UserService userService;

        @Autowired
        private BankAccountService bankAccountService;

        @Autowired
        private ModelMapper modelMapper;


        @GetMapping("/profile")
        public String getProfil(Authentication authentication, Model model) {
                Optional<User> user = userService.findByEmail(authentication.getName());

                if (user.isPresent()) {

                        User currentUser = user.get();

                        // send of userDto of currentUser
                        UserDto userDto = modelMapper.map(currentUser, UserDto.class);

                        userDto.setBankAccountRegistred(currentUser.getBankAccount() != null);
                        userDto.setFullName(currentUser.getFullName());

                        if (!model.containsAttribute("user")) {
                                model.addAttribute("user", userDto);
                        }

                        // send of ApplicationAccount of user (already existed)
                        model.addAttribute("applicationAccount", modelMapper.map(currentUser.getApplicationAccount(), ApplicationAccountDto.class));

                        // send bankAccount of user
                        BankAccountDto bankAccountDto = currentUser.getBankAccount() != null ? modelMapper.map(currentUser.getBankAccount(), BankAccountDto.class) : new BankAccountDto();
                        model.addAttribute("bankAccount", bankAccountDto);

                        // send bankCard of user
                        BankCardDto bankCardDto = currentUser.getBankAccount() != null ? modelMapper.map(currentUser.getBankAccount().getBankCard(), BankCardDto.class) : new BankCardDto();
                        model.addAttribute("bankCard", bankCardDto);

                        return "profile";

                } else {
                        throw new UserNotFoundException("this user with email " + authentication.getName() + " is not registred in application!");
                }
        }

        @PostMapping("/profile/bankaccount")
        public String createBankAccount(Model model, @Valid @ModelAttribute(value = "bankAccount") BankAccountDto bankAccountDto, BindingResult bindingResultBankAccount, @Valid @ModelAttribute(value = "bankCard") BankCardDto bankCardDto, BindingResult bindingResultBankCard,
                        RedirectAttributes redirectAttributes, Authentication authentication) {

                Optional<User> user = userService.findByEmail(authentication.getName());

                if (user.isPresent()) {

                        User currentUser = user.get();

                        // case of errors in form
                        if (bindingResultBankAccount.hasErrors() || bindingResultBankCard.hasErrors()) {
                                model.addAttribute("user", modelMapper.map(currentUser, UserDto.class));
                                model.addAttribute("applicationAccount", modelMapper.map(currentUser.getApplicationAccount(), ApplicationAccountDto.class));

                                return "/profile";
                        }

                        // case of bank account and BankCard correctly fill in
                        BankAccount bankAccount = modelMapper.map(bankAccountDto, BankAccount.class);

                        BankCard bankCard = modelMapper.map(bankCardDto, BankCard.class);

                        // check if bankAccount doesn't already exist
                        Optional<BankAccount> existedBankAccount = bankAccountService.findByIban(bankAccount.getIban());

                        // if bankAccount already exist retrieve it plus bankcard
                        if (existedBankAccount.isPresent()) {
                                bankAccount = existedBankAccount.get();
                                bankCard = bankAccount.getBankCard();
                        }

                        // save of bank account and bankCard of user
                        bankAccount.setBankCard(bankCard);
                        bankAccount.addUser(currentUser);
                        BankAccount userBankAccount = bankAccountService.save(bankAccount);

                        // send of user's bank account
                        redirectAttributes.addFlashAttribute("success", "your bank account was correctly registred.");
                        model.addAttribute("bankAccount", modelMapper.map(userBankAccount, BankAccountDto.class));

                        // send of user
                        UserDto userDto = modelMapper.map(currentUser, UserDto.class);
                        userDto.setBankAccountRegistred(true);
                        userDto.setFullName(currentUser.getFullName());

                        model.addAttribute("user", userDto);

                        // send of applicationAccount of user
                        model.addAttribute("applicationAccount", modelMapper.map(currentUser.getApplicationAccount(), ApplicationAccountDto.class));
                        
                        return "redirect:/profile";

                } else {
                        throw new UserNotFoundException("this user with email " + authentication.getName() + " is not registred in application!");
                }

        }

        @PostMapping("/profile/user")
        public String editUserProfile(@Valid @ModelAttribute(value = "user") UserDto userDto, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, Authentication authentication) {

                Optional<User> user = userService.findByEmail(authentication.getName());
                
                if (user.isPresent()) {

                        User existedUser = user.get();

                        model.addAttribute("applicationAccount", modelMapper.map(existedUser.getApplicationAccount(), ApplicationAccountDto.class));
                        model.addAttribute("bankAccount", modelMapper.map(existedUser.getBankAccount(), BankAccountDto.class));
                        model.addAttribute("bankCard", modelMapper.map(existedUser.getBankAccount().getBankCard(), BankCard.class));

                        if (bindingResult.hasErrors()) {

                                userDto.setEditionProfile(true);
                                return "/profile";
                        }

                        User userToUpdate = modelMapper.map(userDto, User.class);

                        existedUser.setFirstName(userToUpdate.getFirstName());
                        existedUser.setLastName(userToUpdate.getLastName());
                        existedUser.setEmail(userToUpdate.getEmail());
                        existedUser.setPhone(userToUpdate.getPhone());
                        existedUser.setAddress(userToUpdate.getAddress());
                        existedUser.setZip(userToUpdate.getZip());
                        existedUser.setCity(userToUpdate.getCity());

                        User updatedUser = userService.save(existedUser);

                        model.addAttribute("user", updatedUser);

                        return "redirect:/profile";
                } else {
                        throw new UserNotFoundException("this user with email " + authentication.getName() + " is not registred in application!");
                }
        }


}
