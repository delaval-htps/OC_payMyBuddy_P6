package com.paymybuddy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.dto.BankAccountDto;
import com.paymybuddy.dto.BankCardDto;
import com.paymybuddy.dto.ProfileUserDto;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.ApplicationTransaction.TransactionType;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.User;
import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;
import com.paymybuddy.service.ApplicationTransactionService;
import com.paymybuddy.service.BankAccountServiceImpl;
import com.paymybuddy.service.UserService;

@WebMvcTest(controllers = ProfileController.class)

public class ProfileControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private UserService userService;

        @MockBean
        private BankAccountServiceImpl bankAccountService;

        @MockBean
        private ApplicationTransactionService appTransactionService;

        @MockBean
        private CustomUserDetailsService customUserDetailsService;

        @MockBean
        private CustomOAuth2UserService customOAuth2UserService;

        @MockBean
        private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

        @SpyBean
        private ModelMapper modelMapper;

        private static User existedUser;
        private static BankAccount userBankAccount;
        private static BankCard userBankCard;
        private static ApplicationAccount appAccount;
        private static ProfileUserDto profileUserDto;
        private static BankAccountDto bankAccountDto;

        @BeforeEach
        public void inti() {
                existedUser = new User();
                existedUser.setEmail("test@gmail.com");
                existedUser.setLastName("test");
                existedUser.setFirstName("test");

                profileUserDto = new ProfileUserDto();
                profileUserDto.setEmail("new_email_after_edition@gmail.com");
                profileUserDto.setFirstName("success");
                profileUserDto.setLastName("success");

                userBankAccount = new BankAccount();
                userBankAccount.setBalance(1000d);
                userBankAccount.setBic("TESTACOS");
                userBankAccount.setIban("azertyuiopqsdfghjklmwxcvbnazertyuiopqs");
                userBankAccount.setUsers(Set.of(existedUser));

                userBankCard = new BankCard();
                userBankAccount.setBankCard(userBankCard);

                appAccount = new ApplicationAccount();
                appAccount.setAccountNumber("12345");
                appAccount.setBalance(1000d);
                appAccount.setUser(existedUser);

                bankAccountDto = new BankAccountDto();
                bankAccountDto.setBic("TESTACOS");
                bankAccountDto.setIban("azertyuiopqsdfghjklmwxcvbnazertyuiopqs");

        }

        @Test
        @WithMockUser
        void getProfil_whenUserNotfound_thenTrowsException() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

                mockMvc.perform(get("/profile"))
                                .andExpect(result -> {
                                        assertTrue(result.getResolvedException() instanceof UserNotFoundException);
                                });

        }

        @Test
        @WithMockUser
        void getProfil_whenUserFound_thenReturnProfileView() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // set application Account to user
                existedUser.setApplicationAccount(appAccount);

                mockMvc.perform(get("/profile"))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("user", "applicationAccount",
                                                "bankTransaction", "bankAccount", "bankCard"));

        }

        @Test
        @WithMockUser
        void getProfil_whenUserBankAccountNotNull_thenReturnProfileView() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                existedUser.setBankAccount(userBankAccount);
                // set application Account to user
                existedUser.setApplicationAccount(appAccount);

                mockMvc.perform(get("/profile"))
                                .andExpect(status().isOk())
                                .andExpect(model().attributeExists("user", "applicationAccount",
                                                "bankTransaction", "bankAccount", "bankCard"));

        }

        // @Test
        // @WithMockUser
        // void getProfil_whenModelContainsUser_thenReturnProfileView() throws Exception {

        //         when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
        //         existedUser.setBankAccount(userBankAccount);
        //         // set application Account to user
        //         existedUser.setApplicationAccount(appAccount);

        //         Map<String, Object> flashMap = new HashMap<>();
        //         UserDto userDto = new UserDto(null, "test", "test", "test@gmail.com", null, null, null, 0, "password",
        //                         "password", false, false);
                
        //         ApplicationTransactionDto bankTransactionDto = new ApplicationTransactionDto(BigDecimal.TEN,
        //                         "description", "WITHDRAW", "sender@gmail.com", "receiver@gmail.com");
        //         ApplicationAccountDto bankAccountDto = new ApplicationAccountDto(100d);
        //         BankCardDto bankCardDto = new BankCardDto("123456", "1234", "2020-05-12");
        //         flashMap.put("user", userDto);
        //         flashMap.put("bankTransaction", bankTransactionDto);
        //         flashMap.put("bankAccount", bankAccountDto);
        //         flashMap.put("bankcard", bankCardDto);

        //         mockMvc.perform(get("/profile").flashAttrs(flashMap))
        //                         .andExpect(status().isOk())

        //                         .andExpect(model().attributeExists("user", "applicationAccount",
        //                                         "bankTransaction", "bankAccount", "bankCard"));

        // }

        @Test
        @WithMockUser
        void editUserProfil_whenBindingResult_thenReturnProfile() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // put " " in lastname of profileUserDto to for bindingResult to have errors
                profileUserDto.setLastName("");

                mockMvc.perform(post("/profile/user").flashAttr("user", profileUserDto).with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect((result) -> {
                                        assertThat(result.getFlashMap().keySet().equals(new HashSet<>(
                                                        Arrays.asList("error", "user",
                                                                        "org.springframework.validation.BindingResult.user"))));
                                });

                verify(userService, never()).save(Mockito.any(User.class));
                assertThat(profileUserDto.isBankAccountRegistred()).isFalse();
        }

        @Test
        @WithMockUser
        void editUserProfil_whenUserExisted_thenReturnProfile() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                existedUser.setApplicationAccount(appAccount);
                existedUser.setBankAccount(userBankAccount);

                User updatedUser = existedUser;
                updatedUser.setFirstName(profileUserDto.getFirstName());
                updatedUser.setLastName(profileUserDto.getLastName());
                updatedUser.setEmail(profileUserDto.getEmail());

                when(userService.save(Mockito.any(User.class))).thenReturn(updatedUser);
                mockMvc.perform(post("/profile/user").flashAttr("user", profileUserDto).with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andReturn();
                verify(userService, times(1)).save(Mockito.any(User.class));
        }
        @Test
        @WithMockUser
        void editUserProfil_whenUserNotExist_thenThrowUserNotFoundException() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
                

                mockMvc.perform(post("/profile/user").flashAttr("user", profileUserDto).with(csrf()))
                                .andExpect(result -> {
                                        assertTrue(result.getResolvedException() instanceof UserNotFoundException);
                                });

        }

        @Test
        @WithMockUser
        void createBankAccount_whenUserNotfound_thenTrowsException() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
                // initilisation bankAccountDto
                final Map<String, Object> mapBank = new HashMap<>();
                mapBank.put("bankAccount", bankAccountDto);
                // mapBank.put("bankCardDto", bankCardDto);

                mockMvc.perform(post("/profile/bankaccount").param("bank-card-to-add", "true").flashAttrs(mapBank)
                                .with(csrf()))
                                .andExpect(result -> {
                                        assertTrue(result.getResolvedException() instanceof UserNotFoundException);
                                });

        }

        @Test
        @WithMockUser
        void createBankAccount_whenBindingResultBankCard_thenRedirectProfile() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // initilisation bankAccountDto no bindingResult
                final Map<String, Object> mapBank = new HashMap<>();
                mapBank.put("bankAccount", bankAccountDto);
                // initialisation of bankcard with bindingResult
                BankCardDto userBankCardDto = new BankCardDto();
                userBankCard.setCardNumber("");
                mapBank.put("bankCard", userBankCardDto);

                mockMvc.perform(post("/profile/bankaccount").param("bank-card-to-add", "true").flashAttrs(mapBank)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect(flash().attributeExists("error",
                                                "org.springframework.validation.BindingResult.bankCard",
                                                "bankCard"));
                verify(bankAccountService, never()).save(Mockito.any(BankAccount.class));

        }

        @Test
        @WithMockUser
        void createBankAccount_whenBindingResultBankAccount_thenRedirectProfile() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // initilisation bankAccountDto no bindingResult
                final Map<String, Object> mapBank = new HashMap<>();
                BankAccountDto userBankAccountDto = new BankAccountDto();
                userBankAccountDto.setBic("");
                userBankAccountDto.setIban("");
                mapBank.put("bankAccount", userBankAccountDto);

                mockMvc.perform(post("/profile/bankaccount").param("bank-card-to-add", "false").flashAttrs(mapBank)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect(flash().attributeExists("error",
                                                "org.springframework.validation.BindingResult.bankAccount",
                                                "bankAccount"));
                verify(bankAccountService, never()).save(Mockito.any(BankAccount.class));

        }

        @Test
        @WithMockUser
        void createBankAccount_whenUserExistedWithNewBankAccountAndNoBankCard_thenRedirectProfile() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                // initilisation bankAccountDto
                final Map<String, Object> mapBank = new HashMap<>();
                mapBank.put("bankAccount", bankAccountDto);

                when(bankAccountService.findByIban(Mockito.anyString())).thenReturn(Optional.empty());
                when(bankAccountService.save(Mockito.any(BankAccount.class))).thenReturn(userBankAccount);

                mockMvc.perform(post("/profile/bankaccount").param("bank-card-to-add", "false").flashAttrs(mapBank)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect(flash().attributeExists("success", "bankAccount"));

                ArgumentCaptor<Account> bankAccountCaptor = ArgumentCaptor.forClass(Account.class);
                verify(bankAccountService, times(1)).save((BankAccount) bankAccountCaptor.capture());
                assertThat(bankAccountCaptor.getValue()).extracting("bankCard").isNull();

        }

        @Test
        @WithMockUser
        void createBankAccount_whenUserExistedWithWithNewBankAccountAndNewBankCard_thenRedirectProfile()
                        throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                // initilisation bankAccountDto
                final Map<String, Object> mapBank = new HashMap<>();
                mapBank.put("bankAccount", bankAccountDto);
                // initialisation of bankcard with bindingResult
                BankCardDto userBankCardDto = new BankCardDto();
                userBankCardDto.setCardNumber("123456789");
                userBankCardDto.setCardCode("1224");
                userBankCardDto.setExpirationDate("2022-05-12");
                mapBank.put("bankCard", userBankCardDto);

                when(bankAccountService.findByIban(Mockito.anyString())).thenReturn(Optional.empty());
                when(bankAccountService.save(Mockito.any(BankAccount.class))).thenReturn(userBankAccount);

                mockMvc.perform(post("/profile/bankaccount").param("bank-card-to-add", "true").flashAttrs(mapBank)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect(flash().attributeExists("success", "bankAccount"));

                ArgumentCaptor<Account> bankAccountCaptor = ArgumentCaptor.forClass(Account.class);
                verify(bankAccountService, times(1)).save((BankAccount) bankAccountCaptor.capture());
                assertThat(bankAccountCaptor.getValue()).extracting("bankCard").isNotNull();

        }

        @Test
        @WithMockUser
        void createBankAccount_whenUserExistedWithExitedBankAccount_thenRedirectProfile() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                // initilisation bankAccountDto
                final Map<String, Object> mapBank = new HashMap<>();
                mapBank.put("bankAccount", bankAccountDto);

                when(bankAccountService.findByIban(Mockito.anyString())).thenReturn(Optional.of(userBankAccount));
                when(bankAccountService.save(Mockito.any(BankAccount.class))).thenReturn(userBankAccount);

                mockMvc.perform(post("/profile/bankaccount").param("bank-card-to-add", "false").flashAttrs(mapBank)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile")).andExpect(flash().attributeExists("warning"));

                verify(bankAccountService, times(1)).save(Mockito.any(BankAccount.class));

        }

        @Test
        @WithMockUser
        void bankTransaction_whenUserNotfound_thenTrowsException() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

                mockMvc.perform(post("/profile/bank_transaction").with(csrf()))
                                .andExpect(result -> {
                                        assertTrue(result.getResolvedException() instanceof UserNotFoundException);
                                });

        }

        @Test
        @WithMockUser
        void testBankTransaction_whenBindingError_thenRedirectToProfile() throws Exception {
                // when
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // create a applicationTransactionDto but set description to empty to not be
                // validated by javax
                ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
                appTransactionDto.setAmount(BigDecimal.ZERO);
                appTransactionDto.setDescription("");
                appTransactionDto.setSenderEmail("delaval.htps@gmail.com");

                mockMvc.perform(post("/profile/bank_transaction").flashAttr("bankTransaction", appTransactionDto)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect(flash().attributeExists("error",
                                                "org.springframework.validation.BindingResult.bankTransaction",
                                                "bankTransaction"));

        }

        @Test
        @WithMockUser
        void testBankTransaction_whenWithdrawTransaction_thenRedirectToProfile() throws Exception {
                // when
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // create a applicationTransactionDto
                ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
                appTransactionDto.setAmount(BigDecimal.valueOf(100d));
                appTransactionDto.setDescription("bank transaction");
                appTransactionDto.setSenderEmail("delaval.htps@gmail.com");
                appTransactionDto.setReceiverEmail("delaval.htps@gmail.com");
                appTransactionDto.setType("WITHDRAW");

                // mock bankTransaction withdraw
                ApplicationTransaction withdrawBankTransaction = new ApplicationTransaction();
                withdrawBankTransaction.setAmount(100d);
                withdrawBankTransaction.setDescription("bank transaction");
                withdrawBankTransaction.setSender(existedUser);
                withdrawBankTransaction.setReceiver(existedUser);
                withdrawBankTransaction.setType(TransactionType.WITHDRAW);

                when(appTransactionService.proceedBankTransaction(Mockito.any(ApplicationTransaction.class),
                                Mockito.any(User.class))).thenReturn(withdrawBankTransaction);

                mockMvc.perform(post("/profile/bank_transaction").flashAttr("bankTransaction", appTransactionDto)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect(flash().attribute("success", "the withdraw of "
                                                + withdrawBankTransaction.getAmount()
                                                + "€ was correctly realised from your application account to your bank account"));

                verify(appTransactionService, times(1)).proceedBankTransaction(
                                Mockito.any(ApplicationTransaction.class),
                                Mockito.any(User.class));
        }

        @Test
        @WithMockUser
        void testBankTransaction_whenCreditTransaction_thenRedirectToProfile() throws Exception {
                // when
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // create a applicationTransactionDto
                ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
                appTransactionDto.setAmount(BigDecimal.valueOf(100d));
                appTransactionDto.setDescription("bank transaction");
                appTransactionDto.setSenderEmail("delaval.htps@gmail.com");
                appTransactionDto.setReceiverEmail("delaval.htps@gmail.com");
                appTransactionDto.setType("CREDIT");

                // mock bankTransaction withdraw
                ApplicationTransaction withdrawBankTransaction = new ApplicationTransaction();
                withdrawBankTransaction.setAmount(100d);
                withdrawBankTransaction.setDescription("bank transaction");
                withdrawBankTransaction.setSender(existedUser);
                withdrawBankTransaction.setReceiver(existedUser);
                withdrawBankTransaction.setType(TransactionType.CREDIT);

                when(appTransactionService.proceedBankTransaction(Mockito.any(ApplicationTransaction.class),
                                Mockito.any(User.class))).thenReturn(withdrawBankTransaction);

                mockMvc.perform(post("/profile/bank_transaction").flashAttr("bankTransaction", appTransactionDto)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect(result -> {
                                        assertTrue(result.getFlashMap().containsKey("success"));
                                        assertEquals(result.getFlashMap().get("success"), "the credit of "
                                                        + withdrawBankTransaction.getAmount()
                                                        + "€ was correctly realised from your bank account to your application account");
                                });

                verify(appTransactionService, times(1)).proceedBankTransaction(
                                Mockito.any(ApplicationTransaction.class),
                                Mockito.any(User.class));
        }

        @Test
        @WithMockUser
        void testBankTransaction_whenTransactionRollback_thenRedirectToProfile() throws Exception {
                // when
                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // create a applicationTransactionDto
                ApplicationTransactionDto appTransactionDto = new ApplicationTransactionDto();
                appTransactionDto.setAmount(BigDecimal.valueOf(100d));
                appTransactionDto.setDescription("bank transaction");
                appTransactionDto.setSenderEmail("delaval.htps@gmail.com");
                appTransactionDto.setReceiverEmail("delaval.htps@gmail.com");
                appTransactionDto.setType("CREDIT");

                RuntimeException e = new RuntimeException("erreur de transaction");

                when(appTransactionService.proceedBankTransaction(Mockito.any(ApplicationTransaction.class),
                                Mockito.any(User.class))).thenThrow(e);

                mockMvc.perform(post("/profile/bank_transaction").flashAttr("bankTransaction", appTransactionDto)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect(result -> {
                                        assertTrue(result.getFlashMap().containsKey("error"));
                                        assertEquals(result.getFlashMap().get("error"),
                                                        "A problem occured with the transaction, it was not executed: "
                                                                        + e.getMessage()
                                                                        + ". Please retry it or contact us from more information.");
                                });

        }
}
