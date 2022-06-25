package com.paymybuddy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
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
       
        // first connectedUser is found but not existedUser
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(connectedUser), Optional.empty());
        
        mockMvc.perform(post("/transfert/connection").param("email", connectedUser.getEmail()).with(csrf()))
        .andExpect(status().isNotFound()).andDo(print());

    }
   
    @Test
    @WithMockUser(value = "existedUser")
    void testSaveConnectionUser_whenConnectedUserNotFound_thenRedirectWithErrorMessage() throws Exception {
        
        // first  existedUser is found but connectedUser is not found
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty(),Optional.of(existedUser));

        MvcResult result = mockMvc.perform(post("/transfert/connection").param("email", connectedUser.getEmail()).with(csrf())).andExpect(redirectedUrl("/transfert")).andDo(print()).andReturn();

        assertTrue(result.getFlashMap().containsKey("error"));
        assertTrue(result.getFlashMap().containsValue("the user with this email "+connectedUser.getEmail()+" is not registred in application!"));
    }

    @Test
    @WithMockUser(value = "existedUser")
    void testSaveConnectionUser_whenConnectedUserAlreadyConnected_thenRedirectWithWarningMessage() throws Exception {

        // both user are registred and connectedUser already connect with existedUser
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(connectedUser), Optional.of(existedUser));

        // connectedUser already connected with user
        List<User> connectedUsers = new ArrayList<>();
        connectedUsers.add(connectedUser);
        when(userService.findConnectedUserByEmail(Mockito.anyString())).thenReturn(connectedUsers);


        MvcResult result = mockMvc.perform(post("/transfert/connection").param("email", connectedUser.getEmail()).with(csrf())).andExpect(redirectedUrl("/transfert")).andDo(print()).andReturn();

        assertTrue(result.getFlashMap().containsKey("warning"));
        assertTrue(result.getFlashMap().containsValue("the user with this email connectedUser@gmail.com already connected with you!"));
    }
    
    @Test
    @WithMockUser(value = "existedUser")
    void testSaveConnectionUser_whenConnectedUserNotConnectedWithExistedUser_thenRedirectWithSuccessMessage() throws Exception {

       // both user are registred and connectedUser already connect with existedUser
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(connectedUser),Optional.of(existedUser));

        // connectedUser already connected with user
        when(userService.findConnectedUserByEmail(Mockito.anyString())).thenReturn(new ArrayList<>());

        MvcResult result = mockMvc.perform(post("/transfert/connection").param("email",connectedUser.getEmail()).with(csrf())).andExpect(redirectedUrl("/transfert")).andDo(print()).andReturn();

        assertTrue(result.getFlashMap().containsKey("success"));
        assertTrue(result.getFlashMap().containsValue("the user with this email " + connectedUser.getEmail() + " was registred!"));
    }


    @Test
    @WithMockUser
    void testSendMoneyTo_whenBindingErrors_thenRedirectToTransfert() throws Exception {

        // create a applicationTransactionDto but set description to empty to not be validated by javax
        ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
        appTransactionDto.setAmount(BigDecimal.TEN);
        appTransactionDto.setDescription("");

        MvcResult result = mockMvc.perform(post("/transfert/sendmoneyto", appTransactionDto).flashAttr("transaction", appTransactionDto).with(csrf())).andExpect(redirectedUrl("/transfert")).andDo(print()).andReturn();

        assertThat(result.getFlashMap().size()).isEqualTo(3);
        assertThat(result.getFlashMap().get("error")).isEqualTo("a problem has occured in transaction, please check red fields!");
        assertTrue(result.getFlashMap().containsKey("transaction"));
        assertTrue(result.getFlashMap().containsKey("org.springframework.validation.BindingResult.transaction"));
    }
    
    @Test
    @WithMockUser
    void testSendMoneyTo_whenExistedUserNotFound_thenThrowsNotFoundException() throws Exception {

        // create a validated applicationTransactionDto 
        ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
        appTransactionDto.setAmount(BigDecimal.TEN);
        appTransactionDto.setDescription("test");
        appTransactionDto.setReceiverEmail(connectedUser.getEmail());
        appTransactionDto.setSenderEmail(existedUser.getEmail());

        //existed user is not found in bdd
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/transfert/sendmoneyto").flashAttr("transaction", appTransactionDto).with(csrf())).andExpect(status().isNotFound()).andDo(print());

    }

    @Test
    @WithMockUser
    void testSendMoneyTo_whenExistedUserWithNoValidMailOfTransaction_thenThrowsNotFoundException() throws Exception {

        // create a validated applicationTransactionDto but with sender'email different to existedUser 
        ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
        appTransactionDto.setAmount(BigDecimal.TEN);
        appTransactionDto.setDescription("test");
        appTransactionDto.setReceiverEmail(connectedUser.getEmail());
        appTransactionDto.setSenderEmail(connectedUser.getEmail());

        //existed user is not found in bdd
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

        mockMvc.perform(post("/transfert/sendmoneyto").flashAttr("transaction", appTransactionDto).with(csrf())).andExpect(status().isNotFound()).andDo(print());

    }
    
    @Test
    @WithMockUser
    void testSendMoneyTo_whenExistedUserAndNoExistedConnectedUser_thenThrowsNotFoundException() throws Exception {

        // create a validated applicationTransactionDto 
        ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
        appTransactionDto.setAmount(BigDecimal.TEN);
        appTransactionDto.setDescription("test");
        appTransactionDto.setReceiverEmail(connectedUser.getEmail());
        appTransactionDto.setSenderEmail(existedUser.getEmail());

        //existed user is  found in bdd but not for receiver 
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser), Optional.empty());

        mockMvc.perform(post("/transfert/sendmoneyto").flashAttr("transaction", appTransactionDto).with(csrf())).andExpect(status().isNotFound()).andDo(print());

    }
    
    @Test
    @WithMockUser
    void testSendMoneyTo_whenTransactionOk_thenRedirectToTransfert() throws Exception {
       
        connectedUser.setFirstName("delaval");
        connectedUser.setLastName("dorian");
        
        // create a validated applicationTransactionDto 
         applicationTransaction.setAmount(10d);
         applicationTransaction.setDescription("test_transaction");
         applicationTransaction.setReceiver(connectedUser);
         applicationTransaction.setSender(existedUser);
         applicationTransaction.setTransactionDate(new Date());
         applicationTransaction.setAmountCommission(5d);
         ApplicationTransactionDto appTransactionDto = modelMapper.map(applicationTransaction, ApplicationTransactionDto.class);
    
        //receiver and sender existed 
        when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser), Optional.of(connectedUser));
        
        //appTransaction success 
        when(applicationTransactionService.proceedBetweenUsers(Mockito.any(ApplicationTransaction.class), Mockito.any(User.class), Mockito.any(User.class))).thenReturn(applicationTransaction);

        MvcResult result = mockMvc.perform(post("/transfert/sendmoneyto").flashAttr("transaction", appTransactionDto).with(csrf())).andExpect(redirectedUrl("/transfert")).andDo(print()).andReturn();
       
        assertThat(result.getFlashMap().size()).isEqualTo(1);
        assertThat(result.getFlashMap().get("success")).isEqualTo("Transaction of " + appTransactionDto.getAmount() + "€ " + "to " + connectedUser.getFullName() + " was successfull!");
    }
}
