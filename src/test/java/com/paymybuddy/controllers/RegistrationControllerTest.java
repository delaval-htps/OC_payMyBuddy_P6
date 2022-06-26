package com.paymybuddy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.annotation.Before;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.paymybuddy.dto.UserDto;
import com.paymybuddy.model.AuthProvider;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import com.paymybuddy.security.oauth2.user.user_info.GithubUserInfo;
import com.paymybuddy.security.oauth2.user.user_info.OAuth2UserInfo;
import com.paymybuddy.security.oauth2.user.user_info.OAuth2UserInfoFactory;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.ApplicationAccountService;
import com.paymybuddy.service.OAuth2ProviderService;
import com.paymybuddy.service.RoleService;
import com.paymybuddy.service.UserService;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;


@WebMvcTest(controllers = RegistrationController.class)
public class RegistrationControllerTest {

    @MockBean
    private OAuth2ProviderService oAuth2ProviderService;


    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private ApplicationAccountService appAccountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    /**
     * everyone can access to registration without authentication so just test
     * mockMvc.perform(/registration).
     * 
     * @throws Exception
     */
    @Test
    void testRegister_whenUserNotOauth2Login_thenReturnRegistrationPage() throws Exception {

        MvcResult result = mockMvc.perform(get("/registration")).andExpect(status().isOk()).andDo(print()).andReturn();

        assertThat(result.getModelAndView().getModel().size()).isEqualTo(2);
        assertThat(result.getModelAndView().getModel().get("user")).isInstanceOf(UserDto.class);
        Object attribute = result.getModelAndView().getModelMap().getAttribute("user");
        assertThat(attribute instanceof UserDto);
        assertThat(attribute).hasAllNullFieldsOrPropertiesExcept("zip", "duplicatedUser", "editionProfile", "password", "matchingPassword", "bankAccountRegistred");
    }

    /**
     * test with a authentication with OAuth2Login() from Github.
     * 
     * @throws Exception
     */
    @Test
    void testRegister_whenUserInstanceOfCustomOauth2User_thenReturnRegistrationPage() throws Exception {

        /*
         * creation of userDetails for CustomOAuth2User ( all attributes that we need for application) this
         * attriubutes were be used to complete the form of registration page In fact, when a user log with
         * OAuth2 , his lastname firstname and email are retrieve from attributes of CustomOAuht2User and
         * they are displayed in form of registration
         */

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("email", "delaval.htps@gmail.com");
        userDetails.put("name", "dorian delaval");
        userDetails.put("id", "test");

        /*
         * creation of OAuth2User to have possibility to log with our customOAuth2User cretaed just after
         * when MockMvc.perform(/registration).oauth2Login()
         */
        OAuth2User oAuth2User = new DefaultOAuth2User(AuthorityUtils.createAuthorityList("SCOPE_message:read"), userDetails, "name");

        /*
         * Creation of CustomOauth2User to log with it just for this test. A CustomOAuth2User was decided to
         * oblige application to accept only Oauht2login from registrationId Facebook,Github and Google because of OAuth2UserInfoFactory
         */
        CustomOAuth2User mockOauth2User = new CustomOAuth2User(oAuth2User, new GithubUserInfo(userDetails));

        MvcResult result = mockMvc.perform(get("/registration").with(oauth2Login().oauth2User(mockOauth2User))).andExpect(status().isOk()).andDo(print()).andReturn();

        Object attribute = result.getModelAndView().getModelMap().getAttribute("user");

        assertThat(attribute instanceof UserDto);
        UserDto attributeDto = (UserDto) attribute;

        assertThat(attributeDto.getEmail()).isEqualTo("delaval.htps@gmail.com");
        assertThat(attributeDto.getLastName()).isEqualTo("Dorian");
        assertThat(attributeDto.getFirstName()).isEqualTo("Delaval");
    }

    @Test
    void testRegister_whenUserOauth2UserNotInstanceOfCustomOauth2User_thenReturnRegistrationPageWithoutFillInNamesAndEmail() throws Exception {

        MvcResult result = mockMvc.perform(get("/registration").with(oauth2Login())).andExpect(status().isOk()).andDo(print()).andReturn();

        Object attribute = result.getModelAndView().getModelMap().getAttribute("user");

        assertThat(attribute instanceof UserDto);
        UserDto attributeDto = (UserDto) attribute;

        assertThat(attributeDto.getEmail()).isNull();
        assertThat(attributeDto.getLastName()).isNull();
        assertThat(attributeDto.getFirstName()).isNull();
    }



    @Test
    void testSaveNewUser() {

    }
}
