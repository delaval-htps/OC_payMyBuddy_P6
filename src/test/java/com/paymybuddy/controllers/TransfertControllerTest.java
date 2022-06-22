package com.paymybuddy.controllers;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.BankAccount;
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

    @SpyBean
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    private static User existedUser, connectedUser;
    private static BankAccount userBankAccount;
    private static ApplicationTransaction applicationTransaction;
    
    @BeforeAll
    private static void inti() {
        existedUser = new User();
        connectedUser = new User();
        existedUser.setEmail("test@gmail.com");
        connectedUser.setEmail("connectedUser@gmail.com");
        userBankAccount = new BankAccount();
        applicationTransaction = new ApplicationTransaction();
    }

    /**
     * Test getTransfert when user not registred in database or not found.
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser
    void getTransfert_whenNotExistedUser_thenTrowsUserNotFoundException() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/transfert")).andExpect(status().isNotFound()).andDo(print());
    }

    /**
     * Test when user is registred (found) and has no BankAccount
     * 
     * @throws Exception
     */

    @Test
    @WithMockUser
    void getTransfert_whenExistedWithBankAccountNotRegistered_thenRedirectProfile() throws Exception {

        // given : existed user but without bank account
        existedUser.setBankAccount(null);
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        mockMvc.perform(get("/transfert")).andExpect(redirectedUrl("/profile")).andDo(print());

    }

    /**
     * Test when user is registred with a bankAccount and Model doesn't contain a transaction. Case when
     * user arrive on transfert page for first time to make a transaction
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser
    void getTransfert_whenModelNotContainsTransaction_thenReturnTransfert() throws Exception {

        // given : existed user with bank account
     
        userBankAccount.addUser(existedUser);
        userBankAccount.setAccountNumber(12345);
        userBankAccount.setBalance(100d);
        userBankAccount.setBankCode(123);

        existedUser.setBankAccount(userBankAccount);

        applicationTransaction.setAmount(10d);
        applicationTransaction.setDescription("test_transaction");
        applicationTransaction.setReceiver(connectedUser);
        applicationTransaction.setSender(existedUser);
        applicationTransaction.setTransactionDate(new Date());
        applicationTransaction.setAmountCommission(5d);

        existedUser.addSenderTransaction(applicationTransaction);
        connectedUser.addReceiverTransaction(applicationTransaction);

        // mock of userService
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        when(userService.findConnectedUserByEmail(Mockito.anyString())).thenReturn(Arrays.asList(connectedUser));

        // mock of applicationTransfertService
        List<ApplicationTransaction> appTransactions = new ArrayList<>();
        appTransactions.add(applicationTransaction);

        when(applicationTransactionService.findBySender(Mockito.any(User.class))).thenReturn(appTransactions);

        mockMvc.perform(get("/transfert")).andExpect(status().isOk()).andDo(print());

        verify(applicationTransactionService, times(1)).findBySender(Mockito.any(User.class));
    }

    /**
     * Test with existed User with bankAccount but model already contain a transaction between him and
     * his connectedUser. Case : when he just make a transaction and controller return to "/transfert"
     * because of bindingResult with errors (bindingResult is redirect to /transfert with
     * addFlashAttribute and transaction too for display field with errors)
     * 
     * @throws Exception
     */
    @Test
    @WithMockUser
    void getTransfert_whenModelAlreadyContainsTransaction_thenReturnTransfert() throws Exception {

        // given : exited user with bank account
     

        // we create a mock BankAccount for user
        
        userBankAccount.addUser(existedUser);
        userBankAccount.setAccountNumber(12345);
        userBankAccount.setBalance(100d);
        userBankAccount.setBankCode(123);

        existedUser.setBankAccount(userBankAccount);

        // mock of applicationTransactionDto to use with Model
       
        applicationTransaction.setAmount(10d);
        applicationTransaction.setDescription("test_transaction");
        applicationTransaction.setReceiver(connectedUser);
        applicationTransaction.setSender(existedUser);
        applicationTransaction.setTransactionDate(new Date());
        applicationTransaction.setAmountCommission(5d);
        ApplicationTransactionDto appTransactionDto = modelMapper.map(applicationTransaction, ApplicationTransactionDto.class);

        existedUser.addSenderTransaction(applicationTransaction);
        connectedUser.addReceiverTransaction(applicationTransaction);

        // mock of userService
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        when(userService.findConnectedUserByEmail(Mockito.anyString())).thenReturn(Arrays.asList(connectedUser));

        // mock of applicationTransfertService
        List<ApplicationTransaction> appTransactions = new ArrayList<>();
        appTransactions.add(applicationTransaction);

        when(applicationTransactionService.findBySender(Mockito.any(User.class))).thenReturn(appTransactions);

        mockMvc.perform(get("/transfert").flashAttr("transaction", appTransactionDto)).andExpect(status().isOk()).andDo(print());

        verify(applicationTransactionService, times(1)).findBySender(Mockito.any(User.class));
    }

    @Test
    @WithMockUser
    void testSaveConnectionUser_whenAuthenticatedUserNotfound_thenThrowsUserNotFoundException() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser), Optional.empty());

        mockMvc.perform(post("/connection").with(csrf())).andExpect(status().isNotFound()).andDo(print());

    }
    
    @Test
    @WithMockUser
    void testSaveConnectionUser_whenConnectedUserNotFound_thenRedirectWithErrorMessage() throws Exception {

        when(userService.findByEmail(Mockito.anyString())).thenReturn( Optional.empty(),Optional.of(existedUser));

        mockMvc.perform(post("/connection").with(csrf())).andExpect(redirectedUrl("/transfert")).andDo(print());

    }

    @Test
    void testSendMoneyTo() {

    }
}
