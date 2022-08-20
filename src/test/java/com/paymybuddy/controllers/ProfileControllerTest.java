package com.paymybuddy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.dto.ProfileUserDto;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.ApplicationTransactionService;
import com.paymybuddy.service.BankAccountServiceImpl;
import com.paymybuddy.service.UserService;

@WebMvcTest(controllers = ProfileController.class)

public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private BankAccountServiceImpl bankAccountService;

    @MockBean
    private ApplicationTransactionService appTransactionService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @SpyBean

    private ModelMapper modelMapper;
    private static User existedUser;
    private static BankAccount userBankAccount;
    private static ApplicationAccount appAccount;
    private static BankCard userBankCard;
    private static ProfileUserDto profileUserDto;

    @BeforeAll
    private static void inti() {
        existedUser = new User();
        existedUser.setEmail("test@gmail.com");
        existedUser.setLastName("test");
        existedUser.setFirstName("test");
      
        profileUserDto = new ProfileUserDto();
        profileUserDto.setEmail("new_email_after_edition@gmail.com");
        profileUserDto.setFirstName("success");
        profileUserDto.setLastName("success");

        
        userBankCard = new BankCard();
        userBankCard.setCardCode(1234);
        userBankCard.setCardNumber("cardNumber");
        userBankCard.setExpirationDate(new Date());

        userBankAccount = new BankAccount();
        userBankAccount.setBalance(1000d);
        userBankAccount.setBic("BIC");
        userBankAccount.setIban("numberOfIban");
        userBankAccount.setUsers(Set.of(existedUser));

        userBankCard.setBankAccount(userBankAccount);
        userBankAccount.setBankCard(userBankCard);

        appAccount = new ApplicationAccount();
        appAccount.setAccountNumber("12345");
        appAccount.setBalance(1000d);
        appAccount.setUser(existedUser);
    }

    @Test
    @WithMockUser
    void getProfil_whenUserNotfound_thenTrowsException() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/profile"))
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof UserNotFoundException);
                });

    }

    @Test
    @WithMockUser
    void getProfil_whenUserFound_thenReturnProfileView() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        // set application Account to user
          existedUser.setApplicationAccount(appAccount);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk()).andExpect(model().attributeExists("user", "applicationAccount",
                        "bankTransaction", "bankAccount", "bankCard"));

    }

    @Test
    @WithMockUser
    void editUserProfil_whenUserNotfound_thenTrowsException() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/profile/user").with(csrf()))
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof UserNotFoundException);
                });

    }

    @Test
    @WithMockUser
    void editUserProfil_whenUserExisted_thenReturnProfiel() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        existedUser.setApplicationAccount(appAccount);
        existedUser.setBankAccount(userBankAccount);

        User updatedUser = existedUser;
        updatedUser.setFirstName(profileUserDto.getFirstName());
        updatedUser.setLastName(profileUserDto.getLastName());
        updatedUser.setEmail(profileUserDto.getEmail());

        when(userService.save(Mockito.any(User.class))).thenReturn(updatedUser);
        MvcResult result = mockMvc.perform(post("/profile/user").flashAttr("user", profileUserDto).with(csrf()))
                .andExpect(redirectedUrl("/profile"))
                .andExpect(model().attributeExists( "applicationAccount", "bankAccount", "bankCard")).andReturn();
        assertEquals(result.getModelAndView().getModel().get("user"),updatedUser);
        verify(userService, times(1)).save(Mockito.any(User.class));
    }

    @Test
    @WithMockUser
    void createBankAccount_whenUserNotfound_thenTrowsException() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/profile/bankaccount").with(csrf()))
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof UserNotFoundException);
                });

    }

    @Test
    @WithMockUser
    void bankTransaction_whenUserNotfound_thenTrowsException() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/profile/bank_transaction").with(csrf()))
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof UserNotFoundException);
                });

    }

    @Test
    @WithMockUser
    void testBankTransaction_whenBindingError_thenRedirectToProfile() throws Exception {
        // when

        // create a applicationTransactionDto but set description to empty to not be
        // validated by javax
        ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
        appTransactionDto.setAmount(BigDecimal.ZERO);
        appTransactionDto.setDescription("");

        User existedUser = new User();
        existedUser.setLastName("delaval");
        existedUser.setFirstName("Dorian");
        existedUser.setEmail("delaval.htps@gmail.com");

        BankAccount bankAccount = new BankAccount();
        bankAccount.addUser(existedUser);
        bankAccount.setBalance(100d);
        bankAccount.setIban("IbanTest");
        bankAccount.setBic("Bic");

        mockMvc.perform(post("/profile/bank_transaction").flashAttr("bankTransaction", appTransactionDto).with(csrf()))
                .andExpect(redirectedUrl("/profile"));
    }

    @Test
    @WithMockUser
    void testBankTransaction_whenUserExistedandwithdrawTransaction_thenRedirectToProfile() throws Exception {
        // when

        // create a applicationTransactionDto but set description to empty to not be
        // validated by javax
        ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
        appTransactionDto.setAmount(BigDecimal.valueOf(100d));
        appTransactionDto.setDescription("bank transaction");

        User existedUser = new User();
        existedUser.setLastName("delaval");
        existedUser.setFirstName("Dorian");
        existedUser.setEmail("delaval.htps@gmail.com");

        BankAccount bankAccount = new BankAccount();
        bankAccount.addUser(existedUser);
        bankAccount.setBalance(100d);
        bankAccount.setIban("IbanTest");
        bankAccount.setBic("Bic");

        mockMvc.perform(post("/profile/bank_transaction").flashAttr("bankTransaction", appTransactionDto).with(csrf()))
                .andExpect(redirectedUrl("/profile"));
    }

}
