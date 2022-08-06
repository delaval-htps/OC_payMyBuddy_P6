package com.paymybuddy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcResultHandlersDsl;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.paymybuddy.dto.UserDto;
import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import com.paymybuddy.security.oauth2.user.user_info.GithubUserInfo;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.ApplicationAccountServiceImpl;
import com.paymybuddy.service.OAuth2ProviderService;
import com.paymybuddy.service.RoleService;
import com.paymybuddy.service.UserService;


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
    private ApplicationAccountServiceImpl appAccountService;

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
         * oblige application to accept only Oauht2login from registrationId Facebook,Github and Google
         * because of OAuth2UserInfoFactory
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

    void testSaveNewUser_whenBindingResultHasErrors_thenReturnRegistration() throws Exception {
        // when userDto has errors -> all fields are null
        UserDto mockUserDto = new UserDto();

        MvcResult result = mockMvc.perform(post("/registration").flashAttr("user", mockUserDto).with(csrf())).andDo(print()).andReturn();

        assertThat(result.getModelAndView().getViewName()).isEqualTo("registration");
    }

    @Test

    void testSaveNewUser_whenAlreadyExistUserWithSameEmail_thenReturnRegistrationWithBindingResult() throws Exception {
        UserDto mockUserDto = new UserDto();
        mockUserDto.setEmail("test@gmail.com");
        mockUserDto.setFirstName("test");
        mockUserDto.setLastName("test");

        User mockUser = new User();
        mockUser.setEmail("test@gmail.com");
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(mockUser));

        MvcResult result =
                mockMvc.perform(post("/registration").flashAttr("user", mockUserDto).with(csrf())).andExpect(model().attributeHasFieldErrors("user", "duplicatedUser")).andDo(print()).andReturn();

        assertThat(result.getModelAndView().getViewName()).isEqualTo("registration");


    }

    @Test
    void testSaveNewUser_whenNewUserNotOauth2Login_thenRedirectHome() throws Exception {

        //mock of userDto
        UserDto mockUserDto = new UserDto();
        mockUserDto.setEmail("test@gmail.com");
        mockUserDto.setFirstName("test");
        mockUserDto.setLastName("test");
        mockUserDto.setPassword("testPassword");

        // mock UserDetails
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        Set<GrantedAuthority> grantedAuthority = new HashSet<>();
        grantedAuthority.add(simpleGrantedAuthority);

        //mockUser
        User mockUser = new User();
        mockUser.setEmail("test@gmail.com");

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        when(userService.save(Mockito.any(User.class))).thenReturn(mockUser);
        when(customUserDetailsService.loadUserByUsername(Mockito.anyString()))
                .thenReturn(new org.springframework.security.core.userdetails.User(mockUserDto.getEmail(), "testPassword", grantedAuthority));


        mockMvc.perform(post("/registration").flashAttr("user", mockUserDto).with(csrf())).andExpect(redirectedUrl("/home")).andDo(print()).andReturn();


    }

    @Test
   
    void testSaveNewUser_whenNewUserOauth2Login_thenRedirectHome() throws Exception {
   
        //mock of userDto
        UserDto mockUserDto = new UserDto();
        mockUserDto.setEmail("delaval.htps@gmail.com");
        mockUserDto.setFirstName("dorian");
        mockUserDto.setLastName("delaval");
       

        // mock UserDetails
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        Set<GrantedAuthority> grantedAuthority = new HashSet<>();
        grantedAuthority.add(simpleGrantedAuthority);

        //mockUser
        User mockUser = new User();
        mockUser.setEmail("delaval.htps@gmail.com");

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
         * oblige application to accept only Oauht2login from registrationId Facebook,Github and Google
         * because of OAuth2UserInfoFactory
         */
        CustomOAuth2User mockOauth2User = new CustomOAuth2User(oAuth2User, new GithubUserInfo(userDetails));
        
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
        when(userService.save(Mockito.any(User.class))).thenReturn(mockUser);
        when(customUserDetailsService.loadUserByUsername(Mockito.anyString()))
                .thenReturn(new org.springframework.security.core.userdetails.User(mockUser.getEmail(), "testPassword", grantedAuthority));

        MvcResult result = mockMvc.perform(post("/registration").flashAttr("user", mockUserDto).with(oauth2Login().oauth2User(mockOauth2User)).with(csrf())).andExpect(redirectedUrl("/home")).andDo(print()).andReturn();

    }
}
