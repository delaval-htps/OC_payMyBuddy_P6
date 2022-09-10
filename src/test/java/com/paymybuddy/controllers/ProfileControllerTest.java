package com.paymybuddy.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isNull;
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

import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
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
import org.springframework.test.web.servlet.ResultActions;

import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.dto.BankAccountDto;
import com.paymybuddy.dto.BankCardDto;
import com.paymybuddy.dto.ProfileUserDto;
import com.paymybuddy.dto.UserDto;
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
        private static BankCardDto bankCardDto;

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
                userBankAccount.addUser(existedUser);

                userBankCard = new BankCard();

                appAccount = new ApplicationAccount();
                appAccount.setAccountNumber("12345");
                appAccount.setBalance(1000d);
                appAccount.setUser(existedUser);

                bankAccountDto = new BankAccountDto();
                bankAccountDto.setBic("TESTACOS");
                bankAccountDto.setIban("azertyuiopqsdfghjklmwxcvbnazertyuiopqs");

                bankCardDto = new BankCardDto();
                bankCardDto.setCardNumber("1234 4567 1234 1234");
                bankCardDto.setCardCode("123");
                bankCardDto.setExpirationDate("10-25");

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
        void getProfil_whenUserFoundButModelContainsProfileUserDto_thenReturnProfileView() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // set application Account to user
                existedUser.setApplicationAccount(appAccount);

                mockMvc.perform(get("/profile").flashAttr("user", profileUserDto))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("user", Matchers.is(profileUserDto)));

        }

        @Test
        @WithMockUser
        void getProfil_whenUserFoundButModelContainsTransactionDto_thenReturnProfileView() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                // set application Account to user
                existedUser.setApplicationAccount(appAccount);
                ApplicationTransactionDto bankTransactionDto = new ApplicationTransactionDto(BigDecimal.TEN, "bank-transaction",
                               "WITHDRAW", existedUser.getEmail(), existedUser.getEmail());

                mockMvc.perform(get("/profile").flashAttr("bankTransaction", bankTransactionDto))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("bankTransaction", Matchers.is(bankTransactionDto)));

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

        @Test
        @WithMockUser
        void getProfil_whenModelContainsProfileUserDto_thenReturnProfileView() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                existedUser.setBankAccount(userBankAccount);
                // set application Account to user
                existedUser.setApplicationAccount(appAccount);
                existedUser.setBankAccount(userBankAccount);
                existedUser.setBankCard(userBankCard);

                mockMvc.perform(get("/profile"))
                                .andExpect(status().isOk())

                                .andExpect(model().attribute("user",
                                                Matchers.hasProperty("bankAccountRegistred", Matchers.equalTo(true))))
                                .andExpect(model().attribute("user",
                                                Matchers.hasProperty("bankCardRegistred", Matchers.equalTo(true))));

        }

        @Test
        @WithMockUser
        void getProfil_whenModelContainsBankAccountDtoWithBankAccountNotNull_thenReturnProfileView() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                existedUser.setBankAccount(userBankAccount);
                // set application Account to user
                existedUser.setApplicationAccount(appAccount);
                existedUser.setBankAccount(userBankAccount);
                existedUser.setBankCard(userBankCard);

                mockMvc.perform(get("/profile").flashAttr("bankAccount",
                                new BankAccountDto("IBAN-FR78-9456-1234-5645-6891-2341-213", "TESTACCO")))
                                .andExpect(status().isOk())

                                .andExpect(model().attribute("bankAccount",
                                                Matchers.hasProperty("iban", Matchers.endsWith("x"))))
                                .andExpect(model().attribute("bankAccount",
                                                Matchers.hasProperty("bic", Matchers.endsWith("x"))));
        }

        @Test
        @WithMockUser
        void getProfil_whenModelContainsBankAccountDtoWithBankAccountNull_thenReturnProfileView() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                existedUser.setBankAccount(userBankAccount);
                // set application Account to user
                existedUser.setApplicationAccount(appAccount);
                existedUser.setBankAccount(null);
                existedUser.setBankCard(userBankCard);

                mockMvc.perform(get("/profile").flashAttr("bankAccount",
                                new BankAccountDto("IBAN-FR78-9456-1234-5645-6891-2341-213", "TESTACCO")))
                                .andExpect(status().isOk())

                                .andExpect(model().attribute("bankAccount",
                                                Matchers.hasProperty("iban", Matchers.is("IBAN-FR78-9456-1234-5645-6891-2341-213"))))
                                .andExpect(model().attribute("bankAccount",
                                                Matchers.hasProperty("bic", Matchers.is("TESTACCO"))));
        }


        @Test
        @WithMockUser
        void getProfil_whenModelContainsBankCardDtoWithBankCardNotNull_thenReturnProfileView() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                existedUser.setBankAccount(userBankAccount);
                // set application Account to user
                existedUser.setApplicationAccount(appAccount);
                existedUser.setBankAccount(userBankAccount);
                existedUser.setBankCard(userBankCard);

                mockMvc.perform(get("/profile").flashAttr("bankCard",
                                new BankCardDto("1234-1234-1234-1234", "123", "10-25")))
                                .andExpect(status().isOk())

                                .andExpect(model().attribute("bankCard",
                                                Matchers.hasProperty("cardCode", Matchers.endsWith("x"))))
                                .andExpect(model().attribute("bankCard",
                                                Matchers.hasProperty("cardNumber", Matchers.endsWith("x"))));
        }
        @Test
        @WithMockUser
        void getProfil_whenModelContainsBankCardDtoWithBankCardNull_thenReturnProfileView() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                existedUser.setBankAccount(userBankAccount);
                // set application Account to user
                existedUser.setApplicationAccount(appAccount);
                existedUser.setBankAccount(userBankAccount);
                existedUser.setBankCard(null);

                mockMvc.perform(get("/profile").flashAttr("bankCard",
                                new BankCardDto("1234-1234-1234-1234", "123", "10-25")))
                                .andExpect(status().isOk())

                                .andExpect(model().attribute("bankCard",
                                                Matchers.hasProperty("cardCode", Matchers.is("123"))))
                                .andExpect(model().attribute("bankCard",
                                                Matchers.hasProperty("cardNumber", Matchers.is("1234-1234-1234-1234"))));
        }

        @Test
        @WithMockUser
        void getProfil_whenModelContainsNothingAndExistedUserDontHaveBankAccountAndCard_thenReturnProfileView()
                        throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));

                existedUser.setApplicationAccount(appAccount);
                existedUser.setBankAccount(null);
                existedUser.setBankCard(null);

                mockMvc.perform(get("/profile"))
                                .andExpect(status().isOk())
                                .andExpect(model().attribute("bankAccount",
                                                Matchers.hasProperty("iban", Matchers.is(""))))
                                .andExpect(model().attribute("bankAccount",
                                                Matchers.hasProperty("bic", Matchers.is(""))))
                                .andExpect(model().attribute("bankCard",
                                                Matchers.hasProperty("cardCode", Matchers.is(""))))
                                .andExpect(model().attribute("bankCard",
                                                Matchers.hasProperty("cardNumber", Matchers.is(""))));
                ;
        }

        @Test
        @WithMockUser
        void editUserProfil_whenBindingResult_BankAttributesNull_thenReturnProfile() throws Exception {

                when(userService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(existedUser));
                existedUser.setBankAccount(userBankAccount);
                existedUser.setBankCard(userBankCard);

                // put " " in lastname of profileUserDto to for bindingResult to have errors
                profileUserDto.setLastName("");

                mockMvc.perform(post("/profile/user").flashAttr("user", profileUserDto).with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect((result) -> {
                                        assertThat(result.getFlashMap().keySet().equals(new HashSet<>(
                                                        Arrays.asList("error", "user",
                                                                        "org.springframework.validation.BindingResult.user"))));
                                });
                                // .andExpect(model().attribute("user",
                                //                 Matchers.hasProperty("bankAccountRegistred", Matchers.is(true))))
                                //                 .andExpect(model().attribute("user",Matchers.hasProperty("bankCardRegistred",Matchers.is(true))));

                verify(userService, never()).save(Mockito.any(User.class));
            
               
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
                mapBank.put("bankCardDto", bankCardDto);

                mockMvc.perform(post("/profile/bankaccount").param("bank-card-to-add", "false").flashAttrs(mapBank)
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

                verify(bankAccountService, times(1)).save(Mockito.any(BankAccount.class));

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

                mapBank.put("bankCard", bankCardDto);

                when(bankAccountService.findByIban(Mockito.anyString())).thenReturn(Optional.empty());
                when(bankAccountService.save(Mockito.any(BankAccount.class))).thenReturn(userBankAccount);

                mockMvc.perform(post("/profile/bankaccount").param("bank-card-to-add", "true").flashAttrs(mapBank)
                                .with(csrf()))
                                .andExpect(redirectedUrl("/profile"))
                                .andExpect(flash().attributeExists("success", "bankAccount"));

                verify(bankAccountService, times(1)).save(Mockito.any(BankAccount.class));

        }

        @Test
        @WithMockUser
        void createBankAccount_whenUserExistedWithExistedBankAccount_thenRedirectProfile() throws Exception {

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
