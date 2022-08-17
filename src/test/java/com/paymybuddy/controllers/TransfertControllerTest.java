package com.paymybuddy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.ApplicationTransaction.TransactionType;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.pagination.PageItem;
import com.paymybuddy.pagination.PageItem.PageItemType;
import com.paymybuddy.pagination.Paged;
import com.paymybuddy.pagination.Paging;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.ApplicationTransactionService;
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
        private AccountService bankAccountService;

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
         * Test when user is registred with a bankAccount and Model doesn't contain a
         * transaction. Case when user arrive on transfert page for first time to make a
         * transaction
         * 
         * @throws Exception
         */
        @Test
        @WithMockUser
        void getTransfert_whenModelNotContainsTransaction_thenReturnTransfert() throws Exception {

                // given : existed user with bank account

                userBankAccount.addUser(existedUser);
                userBankAccount.setIban("iban-test");
                userBankAccount.setBalance(100d);

                existedUser.setBankAccount(userBankAccount);

                // mock of userService
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                when(userService.findConnectedUserByEmail(Mockito.anyString()))
                                .thenReturn(Arrays.asList(connectedUser));

                // mock of pagination

                when(applicationTransactionService.getPageOfTransaction(existedUser, 0, 5)).thenReturn(null);

                // when
                mockMvc.perform(get("/transfert")).andExpect(status().isOk()).andDo(print());

                // then
                verify(applicationTransactionService, times(1))
                                .getPageOfTransaction(Mockito.any(User.class), anyInt(), anyInt());
        }

        /**
         * Test with existed User with bankAccount but model already contain a
         * transaction between him and
         * his connectedUser.
         * Case : when he just make a transaction and controller* return to "/transfert"
         * because of bindingResult with errors (bindingResult is redirect to /transfert
         * with addFlashAttribute and transaction too for display field with errors)
         * 
         * @throws Exception
         */
        @Test
        @WithMockUser
        void getTransfert_whenModelAlreadyContainsTransaction_thenReturnTransfert() throws Exception {

                // given : exited user with bank account

                // we create a mock BankAccount for user

                userBankAccount.addUser(existedUser);
                userBankAccount.setIban("iban-test");
                userBankAccount.setBalance(100d);

                existedUser.setBankAccount(userBankAccount);

                // mock of applicationTransactionDto to use with Model

                applicationTransaction.setAmount(10d);
                applicationTransaction.setDescription("test_transaction");
                applicationTransaction.setReceiver(connectedUser);
                applicationTransaction.setSender(existedUser);
                applicationTransaction.setTransactionDate(new Date());
                applicationTransaction.setAmountCommission(5d);
                ApplicationTransactionDto appTransactionDto = modelMapper.map(applicationTransaction,
                                ApplicationTransactionDto.class);

                existedUser.addSenderTransaction(applicationTransaction);
                connectedUser.addReceiverTransaction(applicationTransaction);

                // mock of userService
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                when(userService.findConnectedUserByEmail(Mockito.anyString()))
                                .thenReturn(Arrays.asList(connectedUser));

                // mock of pagination

                when(applicationTransactionService.getPageOfTransaction(existedUser, 0, 5)).thenReturn(null);

                // when
                MvcResult result = mockMvc.perform(get("/transfert").flashAttr("transaction", appTransactionDto))
                                .andExpect(status().isOk()).andDo(print()).andReturn();

                // then
                assertThat(result.getModelAndView().getModel()).containsKey("transaction");
                assertThat(result.getModelAndView().getModel().get("transaction")).isEqualTo(appTransactionDto);
                verify(applicationTransactionService, times(1))
                                .getPageOfTransaction(Mockito.any(User.class), anyInt(), anyInt());
        }

        @Test
        @WithMockUser
        void getTransfert_whenModelAlreadyContainsAllReadyPageTransactionFromGetPaging_thenReturnTransfert()
                        throws Exception {

                // given : exited user with bank account

                // we create a mock BankAccount for user

                userBankAccount.addUser(existedUser);
                userBankAccount.setIban("iban-test");
                userBankAccount.setBalance(100d);

                existedUser.setBankAccount(userBankAccount);

                // mock of userService
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                when(userService.findConnectedUserByEmail(Mockito.anyString()))
                                .thenReturn(Arrays.asList(connectedUser));

                // mock of applicationTransactionDto for create Paged

                applicationTransaction.setAmount(10d);
                applicationTransaction.setDescription("test_transaction");
                applicationTransaction.setReceiver(connectedUser);
                applicationTransaction.setSender(existedUser);
                applicationTransaction.setTransactionDate(new Date());
                applicationTransaction.setAmountCommission(5d);
                ApplicationTransactionDto appTransactionDto = modelMapper.map(applicationTransaction,
                                ApplicationTransactionDto.class);

                Paged<ApplicationTransactionDto> paged = new Paged<>();
                paged.setPage(new PageImpl<>(Arrays.asList(appTransactionDto)));
                paged.setPaging(new Paging(1, 5, false, false,
                                Arrays.asList(new PageItem(PageItemType.PAGE, 1, true))));

                // when
                MvcResult result = mockMvc.perform(get("/transfert").flashAttr("userTransactions", paged))
                                .andExpect(status().isOk()).andDo(print()).andReturn();

                // then
                assertThat(result.getModelAndView().getModel()).containsKey("userTransactions");

                assertThat(result.getModelAndView().getModel().get("userTransactions")).isEqualTo(paged);
                verify(applicationTransactionService, never())
                                .getPageOfTransaction(Mockito.any(User.class), anyInt(), anyInt());
        }

        @Test
        @WithMockUser
        void getTransfert_whenModelNotContainsPageTransactionFromGetPaging_thenReturnTransfert()
                        throws Exception {

                // given : exited user with bank account

                // we create a mock BankAccount for user

                userBankAccount.addUser(existedUser);
                userBankAccount.setIban("iban-test");
                userBankAccount.setBalance(100d);

                existedUser.setBankAccount(userBankAccount);

                // mock of userService
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                when(userService.findConnectedUserByEmail(Mockito.anyString()))
                                .thenReturn(Arrays.asList(connectedUser));

                // creation of Paged<ApplicationTransaction>

                applicationTransaction.setAmount(10d);
                applicationTransaction.setDescription("test_transaction");
                applicationTransaction.setReceiver(connectedUser);
                applicationTransaction.setSender(existedUser);
                applicationTransaction.setTransactionDate(new Date());
                applicationTransaction.setAmountCommission(5d);

                Paged<ApplicationTransaction> paged = new Paged<>();
                paged.setPage(new PageImpl<>(Arrays.asList(applicationTransaction)));
                paged.setPaging(new Paging(1, 5, false, false,
                                Arrays.asList(new PageItem(PageItemType.PAGE, 1, true))));

                // mock of pagination return Paged<ApplicationTransaction>
                when(applicationTransactionService.getPageOfTransaction(existedUser, 0, 5)).thenReturn(paged);

                // when
                MvcResult result = mockMvc.perform(get("/transfert"))
                                .andExpect(status().isOk()).andDo(print()).andReturn();

                // then
                assertThat(result.getModelAndView().getModel()).containsKey("userTransactions");

                assertThat(result.getModelAndView().getModel().get("userTransactions")).isNotEqualTo(paged);

                verify(applicationTransactionService, times(1))
                                .getPageOfTransaction(Mockito.any(User.class), anyInt(), anyInt());
        }

        @Test
        @WithMockUser
        void saveConnectionUser_whenAuthenticatedUserNotfound_thenThrowsUserNotFoundException() throws Exception {

                // first connectedUser is found but not existedUser
                when(userService.findByEmail(Mockito.anyString()))
                                .thenReturn(Optional.of(connectedUser), Optional.empty());

                mockMvc.perform(post("/transfert/connection").param("email", connectedUser.getEmail()).with(csrf()))
                                .andExpect(status().isNotFound()).andDo(print());

        }

        @Test
        @WithMockUser(value = "existedUser")
        void saveConnectionUser_whenConnectedUserNotFound_thenRedirectWithErrorMessage() throws Exception {

                // first existedUser is found but connectedUser is not found
                when(userService.findByEmail(Mockito.anyString()))
                                .thenReturn(Optional.empty(), Optional.of(existedUser));

                MvcResult result = mockMvc
                                .perform(post("/transfert/connection").param("email", connectedUser.getEmail())
                                                .with(csrf()))
                                .andExpect(redirectedUrl("/transfert")).andDo(print()).andReturn();

                assertTrue(result.getFlashMap().containsKey("error"));
                assertTrue(result.getFlashMap().containsValue(
                                "the user with this email " + connectedUser.getEmail()
                                                + " is not registred in application!"));
        }

        @Test
        @WithMockUser(value = "existedUser")
        void saveConnectionUser_whenConnectedUserAlreadyConnected_thenRedirectWithWarningMessage()
                        throws Exception {

                // both user are registred and connectedUser already connect with existedUser
                when(userService.findByEmail(Mockito.anyString()))
                                .thenReturn(Optional.of(connectedUser), Optional.of(existedUser));

                // connectedUser already connected with user
                List<User> connectedUsers = new ArrayList<>();
                connectedUsers.add(connectedUser);
                when(userService.findConnectedUserByEmail(Mockito.anyString())).thenReturn(connectedUsers);

                MvcResult result = mockMvc
                                .perform(post("/transfert/connection")
                                                .param("email", connectedUser.getEmail()).with(csrf()))
                                .andExpect(redirectedUrl("/transfert")).andDo(print()).andReturn();

                assertTrue(result.getFlashMap().containsKey("warning"));
                assertTrue(result.getFlashMap()
                                .containsValue("the user with this email connectedUser@gmail.com already connected with you!"));
        }

        @Test
        @WithMockUser(value = "existedUser")
        void saveConnectionUser_whenConnectedUserNotConnectedWithExistedUser_thenRedirectWithSuccessMessage()
                        throws Exception {

                // both user are registred and connectedUser already connect with existedUser
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(connectedUser),
                                Optional.of(existedUser));

                // connectedUser already connected with user
                when(userService.findConnectedUserByEmail(Mockito.anyString())).thenReturn(new ArrayList<>());

                MvcResult result = mockMvc
                                .perform(post("/transfert/connection").param("email", connectedUser.getEmail())
                                                .with(csrf()))
                                .andExpect(redirectedUrl("/transfert")).andDo(print()).andReturn();

                assertTrue(result.getFlashMap().containsKey("success"));
                assertTrue(result.getFlashMap()
                                .containsValue("the user with this email " + connectedUser.getEmail()
                                                + " was registred!"));
        }

        @Test
        @WithMockUser(value = "existedUser")
        void sendMoneyTo_whenBindingErrors_thenRedirectToTransfert() throws Exception {

                // create a applicationTransactionDto but set description to empty to not be
                // validated by javax
                ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
                appTransactionDto.setAmount(BigDecimal.TEN);
                appTransactionDto.setDescription("");

                MvcResult result = mockMvc.perform(post("/transfert/sendmoneyto", appTransactionDto)
                                .flashAttr("transaction", appTransactionDto).with(csrf()))
                                .andExpect(redirectedUrl("/transfert"))
                                .andDo(print()).andReturn();

                assertThat(result.getFlashMap().size()).isEqualTo(3);
                assertThat(result.getFlashMap().get("error"))
                                .isEqualTo("a problem has occured in transaction, please check red fields!");
                assertTrue(result.getFlashMap().containsKey("transaction"));
                assertTrue(result.getFlashMap()
                                .containsKey("org.springframework.validation.BindingResult.transaction"));
        }

        @Test
        @WithMockUser
        void sendMoneyTo_whenExistedUserNotFound_thenThrowsNotFoundException() throws Exception {

                // create a validated applicationTransactionDto
                ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
                appTransactionDto.setAmount(BigDecimal.TEN);
                appTransactionDto.setType(TransactionType.WITHDRAW.toString());
                appTransactionDto.setDescription("test");
                appTransactionDto.setReceiverEmail(connectedUser.getEmail());
                appTransactionDto.setSenderEmail(existedUser.getEmail());

                // existed user is not found in bdd
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

                mockMvc.perform(post("/transfert/sendmoneyto").flashAttr("transaction", appTransactionDto).with(csrf()))
                                .andExpect(status().isNotFound()).andDo(print());

        }

        @Test
        @WithMockUser
        void sendMoneyTo_whenExistedUserWithNoValidMailOfTransaction_thenThrowsNotFoundException()
                        throws Exception {

                // create a validated applicationTransactionDto but with sender'email different
                // to existedUser
                ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
                appTransactionDto.setAmount(BigDecimal.TEN);
                appTransactionDto.setType(TransactionType.WITHDRAW.toString());
                appTransactionDto.setDescription("test");
                appTransactionDto.setReceiverEmail(connectedUser.getEmail());
                appTransactionDto.setSenderEmail(connectedUser.getEmail());

                // existed user is not found in bdd
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                mockMvc.perform(post("/transfert/sendmoneyto").flashAttr("transaction", appTransactionDto).with(csrf()))
                                .andExpect(status().isNotFound()).andDo(print());

        }

        @Test
        @WithMockUser
        void sendMoneyTo_whenExistedUserAndNoExistedConnectedUser_thenThrowsNotFoundException() throws Exception {

                // create a validated applicationTransactionDto
                ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
                appTransactionDto.setAmount(BigDecimal.TEN);
                appTransactionDto.setType(TransactionType.WITHDRAW.toString());
                appTransactionDto.setDescription("test");
                appTransactionDto.setReceiverEmail(connectedUser.getEmail());
                appTransactionDto.setSenderEmail(existedUser.getEmail());

                // existed user is found in bdd but not for receiver
                when(userService.findByEmail(Mockito.anyString()))
                                .thenReturn(Optional.of(existedUser), Optional.empty());

                mockMvc.perform(post("/transfert/sendmoneyto").flashAttr("transaction", appTransactionDto).with(csrf()))
                                .andExpect(status().isNotFound()).andDo(print());

        }

        @Test
        @WithMockUser
        void sendMoneyTo_whenTransactionOk_thenRedirectToTransfert() throws Exception {

                connectedUser.setFirstName("delaval");
                connectedUser.setLastName("dorian");

                // create a validated applicationTransactionDto
                applicationTransaction.setAmount(10d);
                applicationTransaction.setDescription("test_transaction");
                applicationTransaction.setReceiver(connectedUser);
                applicationTransaction.setType(TransactionType.WITHDRAW);
                applicationTransaction.setSender(existedUser);
                applicationTransaction.setTransactionDate(new Date());
                applicationTransaction.setAmountCommission(5d);
                ApplicationTransactionDto appTransactionDto = modelMapper.map(applicationTransaction,
                                ApplicationTransactionDto.class);

                // receiver and sender existed
                when(userService.findByEmail(Mockito.anyString()))
                                .thenReturn(Optional.of(existedUser), Optional.of(connectedUser));

                // appTransaction success
                when(applicationTransactionService.proceedBetweenUsers(Mockito.any(ApplicationTransaction.class),
                                Mockito.any(User.class), Mockito.any(User.class))).thenReturn(applicationTransaction);

                MvcResult result = mockMvc.perform(post("/transfert/sendmoneyto")
                                .flashAttr("transaction", appTransactionDto).with(csrf()))
                                .andExpect(redirectedUrl("/transfert")).andDo(print()).andReturn();

                assertThat(result.getFlashMap().size()).isEqualTo(1);
                assertThat(result.getFlashMap().get("success"))
                                .isEqualTo("Transaction of " + appTransactionDto.getAmount()
                                                + "€ " + "to " + connectedUser.getFullName() + " was successfull!");
        }
}
