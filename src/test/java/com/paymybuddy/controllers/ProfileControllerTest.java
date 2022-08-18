package com.paymybuddy.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import java.math.BigDecimal;
import java.util.Optional;

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

import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.BankAccount;
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
    private static User existedUser, connectedUser;
    private static BankAccount userBankAccount;
   

    @BeforeAll
    private static void inti() {
        existedUser = new User();
        connectedUser = new User();
        existedUser.setEmail("test@gmail.com");
        connectedUser.setEmail("connectedUser@gmail.com");
        userBankAccount = new BankAccount();
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
    void editUserProfil_whenUserNotfound_thenTrowsException() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/profile/user").with(csrf()))
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof UserNotFoundException);
                });

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
