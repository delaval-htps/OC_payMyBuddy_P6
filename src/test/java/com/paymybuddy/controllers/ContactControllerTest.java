package com.paymybuddy.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.UserService;

@WebMvcTest(controllers = ContactController.class)
public class ContactControllerTest {

    @MockBean
    private UserService userService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Test
    @WithMockUser
    void getContact_whenUserNotRegistred_thenRedirectLogout() throws Exception {
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        mockMvc.perform(get("/contact")).andExpect(redirectedUrl("/logout"));

    }

    @Test

    @WithMockUser
    void getContact_whenUserRegistred_thenReturnContact() throws Exception {
        User existedUser = new User();
        existedUser.setEmail("test@gmail.com");
        existedUser.setLastName("test1");
        existedUser.setFirstName("test3");

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
        mockMvc.perform(get("/contact")).andExpect(status().isOk());

    }

}
