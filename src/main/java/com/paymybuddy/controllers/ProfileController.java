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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.paymybuddy.dto.ApplicationAccountDto;
import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.dto.BankAccountDto;
import com.paymybuddy.dto.BankCardDto;
import com.paymybuddy.dto.ProfileUserDto;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;
import com.paymybuddy.model.ApplicationTransaction.TransactionType;
import com.paymybuddy.service.ApplicationTransactionService;
import com.paymybuddy.service.BankAccountServiceImpl;
import com.paymybuddy.service.UserService;

/**
 * Controller to manage the profuile page of application.
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

        private static final String ERROR = "error";
        private static final String BANK_TRANSACTION = "bankTransaction";
        private static final String BANK_ACCOUNT = "bankAccount";
        private static final String REDIRECT_PROFILE = "redirect:/profile";
        @Autowired
        private UserService userService;

        @Autowired
        private BankAccountServiceImpl bankAccountService;

        @Autowired
        private ApplicationTransactionService appTransactionService;

        @Autowired
        private ModelMapper modelMapper;

        /**
         * return view with profile and bankAccount's' information of user.
         * If bankAccount information are not defined - user can fill form to perform
         * it.
         * 
         * @param authentication authentication of user
         * @param model          model to add attributes
         * @return the view of profile
         */
        @GetMapping("")
        public String getProfil(Authentication authentication, Model model) {
                Optional<User> user = userService.findByEmail(authentication.getName());

                if (user.isPresent()) {

                        User currentUser = user.get();

                        // send of userDto of currentUser
                        ProfileUserDto profileUserDto = modelMapper.map(currentUser, ProfileUserDto.class);

                        profileUserDto.setBankAccountRegistred(currentUser.getBankAccount() != null);
                        profileUserDto.setFullName(currentUser.getFullName());

                        if (!model.containsAttribute("user")) {
                                model.addAttribute("user", profileUserDto);
                        }

                        // send of ApplicationAccount of user (already existed)
                        model.addAttribute("applicationAccount",
                                        modelMapper.map(currentUser.getApplicationAccount(),
                                                        ApplicationAccountDto.class));

                        // send bankTransaction
                        if (!model.containsAttribute(BANK_TRANSACTION)) {
                                model.addAttribute(BANK_TRANSACTION, new ApplicationTransactionDto());
                        }

                        // send bankAccount of user
                        if (!model.containsAttribute(BANK_ACCOUNT)) {
                                BankAccountDto bankAccountDto = (currentUser.getBankAccount() != null)
                                                ? modelMapper.map(currentUser.getBankAccount(), BankAccountDto.class)
                                                : new BankAccountDto();
                                model.addAttribute(BANK_ACCOUNT, bankAccountDto);
                        }
                        // send bankCard of user 
                        if (!model.containsAttribute("bankCard")) {
                                BankCardDto bankCardDto = (currentUser.getBankAccount() != null && currentUser.getBankAccount().getBankCard() != null)
                                                ? modelMapper.map(currentUser.getBankAccount().getBankCard(),
                                                                BankCardDto.class)
                                                : new BankCardDto();
                                model.addAttribute("bankCard", bankCardDto);
                        }
                        return "profile";

                } else {
                        throw new UserNotFoundException("this user with email " + authentication.getName()
                                        + " is not registred in application!");
                }
        }

        /**
         * method to update profile of user, only personnal information about him.
         * Generally, his already have a applicationAccount, a bankAccount and a
         * bankcard because edit the profile need to fill in form of this attributes.
         * 
         * @param profileUserDto     the Dto of connected user
         * @param bindingResult      the binding result if there is errors when fill in
         *                           form
         *                           of profile
         * @param redirectAttributes allow to send flashAttribute in model
         * @param authentication     authentication of user
         * @return the view of profile.
         */
        @PostMapping("/user")
        public String editUserProfile(@Valid @ModelAttribute(value = "user") ProfileUserDto profileUserDto,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes,
                        Authentication authentication) {

                Optional<User> user = userService.findByEmail(authentication.getName());

                if (user.isPresent()) {

                        User existedUser = user.get();

                        if (bindingResult.hasErrors()) {
                                redirectAttributes.addFlashAttribute(ERROR,
                                                "a problem has occured when editing profile, please check red fields!");

                                redirectAttributes.addFlashAttribute(
                                                "org.springframework.validation.BindingResult.user",
                                                bindingResult);

                                profileUserDto.setBankAccountRegistred(existedUser.getBankAccount() != null);
                                profileUserDto.setFullName(user.get().getFullName());

                                // for keep on profile form
                                profileUserDto.setEditionProfile(true);

                                redirectAttributes.addFlashAttribute("user", profileUserDto);
                        } else {

                                User userToUpdate = modelMapper.map(profileUserDto, User.class);

                                existedUser.setFirstName(userToUpdate.getFirstName());
                                existedUser.setLastName(userToUpdate.getLastName());
                                existedUser.setEmail(userToUpdate.getEmail());
                                existedUser.setPhone(userToUpdate.getPhone());
                                existedUser.setAddress(userToUpdate.getAddress());
                                existedUser.setZip(userToUpdate.getZip());
                                existedUser.setCity(userToUpdate.getCity());

                                userService.save(existedUser);
                        }
                } else {
                        throw new UserNotFoundException("this user with email " + authentication.getName()
                                        + " is not registred in application!");
                }

                return REDIRECT_PROFILE;
        }

        /**
         * PostMapping to save bank account of connected user by form.
         * 
         * @param model                    model to add attributes to next view page.
         * @param bankAccountDto           a bankAccountDto recevive from form with
         *                                 informations about bank account of user.
         * @param bindingResultBankAccount the binding result for bankAccount in case of
         *                                 validation error
         * @param bankCardDto              the bankCardDto receive from form with
         *                                 informations about user's card
         * @param bindingResultBankCard    the bindig result of bankcardDto incase of
         *                                 validation error
         * @param redirectAttributes       redirectAttribute to send attirbutes by using
         *                                 redirection
         * @param authentication           authentication of connected user
         * @return the view page Profile either by redirection or not
         */
        @PostMapping("/bankaccount")
        public String createBankAccount(Model model,
                        @RequestParam(value = "bank-card-to-add") boolean addBankCard,
                        @Valid @ModelAttribute(value = BANK_ACCOUNT) BankAccountDto bankAccountDto,
                        BindingResult bindingResultBankAccount,
                        @Valid @ModelAttribute(value = "bankCard") BankCardDto bankCardDto,
                        BindingResult bindingResultBankCard, RedirectAttributes redirectAttributes,
                        Authentication authentication) {

                Optional<User> user = userService.findByEmail(authentication.getName());

                // case of errors in form
                if (bindingResultBankAccount.hasErrors()) {
                        redirectAttributes.addFlashAttribute(ERROR,
                                        "a problem has occured, please check red fields!");
                        redirectAttributes.addFlashAttribute(
                                        "org.springframework.validation.BindingResult.bankAccount",
                                        bindingResultBankAccount);
                        redirectAttributes.addFlashAttribute(BANK_ACCOUNT, bankAccountDto);

                } else if (user.isPresent()) {

                        User currentUser = user.get();

                        // check if bank account and BankCard correctly fill in
                        BankAccount bankAccount = modelMapper.map(bankAccountDto, BankAccount.class);

                        BankCard bankCard = new BankCard();

                        if (addBankCard) {

                                if (bindingResultBankCard.hasErrors()) {
                                        redirectAttributes.addFlashAttribute(ERROR,
                                                        "a problem has occured, please check red fields!");
                                        redirectAttributes.addFlashAttribute(
                                                        "org.springframework.validation.BindingResult.bankCard",
                                                        bindingResultBankCard);
                                        redirectAttributes.addFlashAttribute("bankCard", bankCardDto);
                                        return REDIRECT_PROFILE;
                                }

                                bankCard = modelMapper.map(bankCardDto, BankCard.class);

                        }

                        // check if bankAccount doesn't already exist
                        Optional<BankAccount> existedBankAccount = bankAccountService.findByIban(bankAccount.getIban());

                        // if bankAccount already exist retrieve it plus bankcard
                        if (existedBankAccount.isPresent()) {
                                // just a warning because we want to able parents to have commun BankAccount.
                                redirectAttributes.addFlashAttribute("warning",
                                                "This bank account already exist:If you want to change it please contact us by email.");

                        }

                        // save of bank account and bankCard of user
                        bankAccount.setBankCard(addBankCard?bankCard:null);
                        bankAccount.addUser(currentUser);
                        BankAccount userBankAccount = bankAccountService.save(bankAccount);

                        // send of user's bank account
                        redirectAttributes.addFlashAttribute("success",
                                        "your bank account was correctly registred.");

                        redirectAttributes.addFlashAttribute(BANK_ACCOUNT,
                                        modelMapper.map(userBankAccount, BankAccountDto.class));

                } else {
                        throw new UserNotFoundException("this user with email " + authentication.getName()
                                        + " is not registred in application!");
                }
                return REDIRECT_PROFILE;
        }

        /**
         * PostMapping that represents a user transaction to either withdraw or credit
         * his bank account with a amount
         * 
         * @param authentication            authentication of connected user
         * @param applicationTransactionDto applicationTransactionDto to receive
         *                                  informations about transaction to proceed
         *                                  with user's bank
         * @param bindingResult             bindingResult if validation error with
         *                                  transaction
         * @param redirectAttributes        to send message and add attributes when
         *                                  redirected
         * @return the page of profile with or not redirection
         */
        @PostMapping("/bank_transaction")
        public String bankTransaction(Authentication authentication,
                        @Valid @ModelAttribute(name = BANK_TRANSACTION) ApplicationTransactionDto applicationTransactionDto,
                        BindingResult bindingResult, RedirectAttributes redirectAttributes) {

                Optional<User> user = userService.findByEmail(applicationTransactionDto.getSenderEmail());

                if (user.isEmpty()) {
                        throw new UserNotFoundException("the user was not found, transaction was cancel!");
                }

                if (bindingResult.hasErrors()) {
                        redirectAttributes.addFlashAttribute(ERROR,
                                        "a problem has occured in transaction, please check red fields!");

                        redirectAttributes.addFlashAttribute(
                                        "org.springframework.validation.BindingResult.bankTransaction", bindingResult);

                        redirectAttributes.addFlashAttribute(BANK_TRANSACTION, applicationTransactionDto);

                } else {

                        User existedUser = user.get();

                        ApplicationTransaction bankTransaction = modelMapper.map(applicationTransactionDto,
                                        ApplicationTransaction.class);

                        try {
                                ApplicationTransaction executedBankTransaction = appTransactionService
                                                .proceedBankTransaction(bankTransaction, existedUser);

                                String messageTransaction = executedBankTransaction.getType()
                                                .equals(TransactionType.WITHDRAW)
                                                                ? "from your application account to your bank account"
                                                                : "from your bank account to your application account";

                                redirectAttributes.addFlashAttribute("success",
                                                "the " + executedBankTransaction.getType().toString().toLowerCase()
                                                                + " of "
                                                                + executedBankTransaction.getAmount()
                                                                + "€ was correctly realised " + messageTransaction);

                        } catch (Exception e) {
                                redirectAttributes.addFlashAttribute(ERROR,
                                                "A problem occured with the transaction, it was not executed: "
                                                                + e.getMessage()
                                                                + ". Please retry it or contact us from more information.");
                        }
                }
                return REDIRECT_PROFILE;

        }

}
