package com.paymybuddy.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.UserService;

@WebMvcTest(controllers = ProfileController.class)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AccountService bankAccountService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @SpyBean
    private ModelMapper modelMapper;

    @Test
    @WithMockUser
    void testBankTransaction_whenBindingError_thenRedirectToProfile() throws Exception {
        // when

        // create a applicationTransactionDto but set description to empty to not be validated by javax
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

        mockMvc.perform(post("/profile/bank_transaction").flashAttr("bankTransaction", appTransactionDto).with(csrf())).andExpect(redirectedUrl("/profile"));
    }
    @Test
    @WithMockUser
    void testBankTransaction_whenUserExistedandwithdrawTransaction_thenRedirectToProfile() throws Exception {
        // when

        // create a applicationTransactionDto but set description to empty to not be validated by javax
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

        mockMvc.perform(post("/profile/bank_transaction").flashAttr("bankTransaction", appTransactionDto).with(csrf())).andExpect(redirectedUrl("/profile"));
    }


}
