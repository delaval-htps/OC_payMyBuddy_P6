package com.paymybuddy;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
class PayMyBuddySecurityConfigIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext context;



  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  void shouldReturnLoginPage() throws Exception {
    mockMvc.perform(get("/myLoginPage")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  void userLoginTest_WhenUserExist_ShouldReturnAuthenticated() throws Exception {
    mockMvc.perform(formLogin("/authenticateTheUser")
        .user("delaval.htps@gmail.com")
        .password("Jsadmin4all"))
        .andDo(print())
        .andExpect(authenticated());
  }

  @Test
  void userLoginTest_WhenUserNotExists_ShouldReturnNoAuthenticated() throws Exception {
    mockMvc.perform(formLogin("/authenticateTheUser")
        .user("delaval.htps@gmail.com")
        .password("passwordNotValid"))
        .andDo(print())
        .andExpect(unauthenticated());
  }

}
