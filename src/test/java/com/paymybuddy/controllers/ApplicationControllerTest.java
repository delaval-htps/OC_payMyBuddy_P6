package com.paymybuddy.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.hamcrest.beans.HasPropertyWithValue;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.UserService;

@WebMvcTest(controllers = ApplicationController.class)

public class ApplicationControllerTest {

    @MockBean
    private UserService userService;

    @MockBean(name = "customUserDetailsService")
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandlerUserService;

    @SpyBean
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void testGetHome_whenUserNotFound_whenRedirectLogout() throws Exception {
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/home")).andExpect(status().isFound()).andExpect(redirectedUrl("/logout"));

    }

    @Test
    @WithAnonymousUser
    void testGetHome_whenNoAuthentication_whenRedirectLogout() throws Exception {

        mockMvc.perform(get("/home")).andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/loginPage"));

    }

    @Test
    @WithMockUser
    void testGetHome_whenUserOkWithApplicationAccount_whenReturnHome() throws Exception {
        // given : existed authenticated user has already a application account
        // because at registration we automatically create a application account for him

        User existedUser = new User();
        existedUser.setEmail("test@gmail.com");
        existedUser.setFirstName("test");
        existedUser.setLastName("test");

        // As we put application account in model line 44 in ApplicationController, we
        // have to assign him
        // one application account
        ApplicationAccount appApplicationAccount = new ApplicationAccount();
        appApplicationAccount.setAccountNumber("numberTest");
        appApplicationAccount.setBalance(100d);
        appApplicationAccount.setUser(existedUser);

        existedUser.setApplicationAccount(appApplicationAccount);

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        mockMvc.perform(get("/home")).andExpect(status().isOk());

    }

    @Test
    @WithMockUser
    void testGetHome_whenUserBankAccountNotNull_whenReturnHome() throws Exception {
        // given : existed authenticated user has already a application account
        // because at registration we automatically create a application account for him
        User existedUser = new User();
        existedUser.setEmail("test@gmail.com");
        existedUser.setFirstName("test");
        existedUser.setLastName("test");

        // And we create for him a bankAccount only for missing branch coverage
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("iban_test");
        bankAccount.setBalance(100d);

        existedUser.setBankAccount(bankAccount);

        // As we put application account in model line 44 in ApplicationController, we
        // have to assign him
        // one application account
        ApplicationAccount appApplicationAccount = new ApplicationAccount();
        appApplicationAccount.setAccountNumber("numberTest");
        appApplicationAccount.setBalance(100d);
        appApplicationAccount.setUser(existedUser);

        existedUser.setApplicationAccount(appApplicationAccount);

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        mockMvc.perform(get("/home")).andExpect(status().isOk())
        .andExpect(model().attribute("user", Matchers.hasProperty("bankAccountRegistred", Matchers.equalTo(true))));

    }

    @Test
    @WithMockUser
    void testGetHome_whenUserBankCardNotNull_whenReturnHome() throws Exception {
        // given : existed authenticated user has already a application account
        // because at registration we automatically create a application account for him
        User existedUser = new User();
        existedUser.setEmail("test@gmail.com");
        existedUser.setFirstName("test");
        existedUser.setLastName("test");

        // And we create for him a bankAccount only for missing branch coverage
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("iban_test");
        bankAccount.setBalance(100d);

        BankCard bankCard = new BankCard();
        bankCard.setCardNumber(("1234 1234 1234 1234"));
        bankCard.setCardCode(123);
        bankCard.setExpirationDate("10-35");

        existedUser.setBankAccount(bankAccount);
        existedUser.setBankCard(bankCard);

        // As we put application account in model line 44 in ApplicationController, we
        // have to assign him
        // one application account
        ApplicationAccount appApplicationAccount = new ApplicationAccount();
        appApplicationAccount.setAccountNumber("numberTest");
        appApplicationAccount.setBalance(100d);
        appApplicationAccount.setUser(existedUser);

        existedUser.setApplicationAccount(appApplicationAccount);

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        mockMvc.perform(get("/home")).andExpect(status().isOk())
                .andExpect(model().attribute("user", Matchers.hasProperty("bankCardRegistred", Matchers.equalTo(true))));

    }

}
