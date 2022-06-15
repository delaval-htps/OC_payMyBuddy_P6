package com.paymybuddy.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.ApplicationTransactionService;
import com.paymybuddy.service.BankAccountService;
import com.paymybuddy.service.UserService;

@WebMvcTest(controllers = TransfertController.class)
public class TransfertControllerTest {

    @MockBean
    private UserService userService;
    
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @MockBean
    private ApplicationTransactionService applicationTransactionService;

    @MockBean
    private BankAccountService bankAccountService;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void getTransfert_whenUserBankAccountNotRegistered_thenRedirectProfile() throws Exception {

        // given : exited user but without bank account
        User existedUser = new User();
        existedUser.setEmail("test@gmail.com");

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        mockMvc.perform(get("/transfert")).andExpect(redirectedUrl("/profile")).andDo(print());

    }

    @Test
    void testSaveConnectionUser() {

    }

    @Test
    void testSendMoneyTo() {

    }
}
