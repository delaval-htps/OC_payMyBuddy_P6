package com.paymybuddy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.UserService;

@WebMvcTest(controllers = ApplicationController.class)
@Sql( "/data-test.sql")
public class ApplicationControllerTest {

    @MockBean
    private UserService userService;

    @MockBean(name = "customUserDetailsService")
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandlerUserService;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    @PostConstruct
    public void setup() {
        User existedUser = new User();
        existedUser.setEmail("delaval.htps@gmail.com");
        existedUser.setPassword("Jsadmin4all");
        
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        UserDetails user = new org.springframework.security.core.userdetails.User(existedUser.getEmail(), existedUser.getPassword(), grantedAuthorities);
        when(customUserDetailsService.loadUserByUsername(Mockito.anyString())).thenReturn(user);
    }

    @Test
    void testGetHome_whenUserNotFound_whenRedirectLogout() throws Exception {

        mockMvc.perform(get("/home")).andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/loginPage")).andDo(print());

    }

    @Test
    @WithAnonymousUser
    void testGetHome_whenNoAuthentication_whenRedirectLogout() throws Exception {

        mockMvc.perform(get("/home")).andExpect(status().isFound()).andExpect(redirectedUrl("http://localhost/loginPage")).andDo(print());

    }

    @Test
    @WithUserDetails(setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void testGetHome_whenUserOk_whenReturnHome() throws Exception {

        MvcResult result = mockMvc.perform(get("/home")).andExpect(status().isFound()).andDo(print()).andReturn();
        assertThat(result.getModelAndView().getViewName()).isEqualTo("/home");
    }



}
