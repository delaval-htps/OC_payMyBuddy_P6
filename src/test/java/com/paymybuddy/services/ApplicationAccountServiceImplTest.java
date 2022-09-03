package com.paymybuddy.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.UtilService;
import com.paymybuddy.exceptions.ApplicationAccountException;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ApplicationAccountRepository;
import com.paymybuddy.service.ApplicationAccountServiceImpl;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class ApplicationAccountServiceImplTest {

    @Mock
    private ApplicationAccountRepository appAccountRepository;

    @Mock
    private UtilService utilService;

    @InjectMocks
    private ApplicationAccountServiceImpl cut;

    static private ApplicationAccount appAccount = new ApplicationAccount();
    static private User user = new User();


    @BeforeAll
    public static void setUp() {
        user.setLastName("delaval");
        user.setFirstName("dorian");
        user.setEmail("dorian.delaval@gmail.com");

        appAccount.setId(1L);
        appAccount.setAccountNumber("accountNumber");
        appAccount.setBalance(1000d);
        appAccount.setUser(user);
        user.setApplicationAccount(appAccount);
    }

    @Test
    @Order(1)
    void testFindById_whenAppAccountExisted_thenReturnOptionalAppAccount() {

        when(appAccountRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(appAccount));
        Optional<ApplicationAccount> result = cut.findById(1L);
        assertThat(result).isNotEmpty().contains(appAccount);
    }

    @Test
    @Order(2)
    void testFindById_whenAppAccountNotExisted_thenReturnOptionalAppAccount() {

        when(appAccountRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Optional<ApplicationAccount> result = cut.findById(1L);
        assertThat(result).isEmpty();
    }


    @Test
    @Order(3)
    void testFindByEmail_whenUserExisted_thenReturnHisAppAccount() {

        when(appAccountRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(appAccount));

        Optional<ApplicationAccount> result = cut.findByEmail("dorian.delaval@gmail.com");

        assertThat(result).isNotEmpty().contains(appAccount);
    }

    @Test
    @Order(4)
    void testFindByEmail_whenUserNotExisted_thenReturnEmptyOptional() {

        when(appAccountRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Optional<ApplicationAccount> result = cut.findByEmail("dorian.delaval@gmail.com");

        assertThat(result).isEmpty();
    }


    @Test
    @Order(5)
    void testSave_whenAppAccountExisted() {

        when(appAccountRepository.save(Mockito.any(ApplicationAccount.class))).thenReturn(appAccount);

        Account result = cut.save(appAccount);

        assertThat(result).isEqualTo(appAccount);
    }

    @Test
    @Order(6)
    void testSave_whenAppAccountNull() {

        ApplicationAccount nullAccount = null;
        when(appAccountRepository.save(null)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> {
            cut.save(nullAccount);
        });
    }

    @Test
    @Order(7)
    void testCreateAccountforUser_whenUserRegistredAndDontHaveAppAcount_thenReturnHisNewAppAccount()  {
        User userWithAppAccount = new User();
        userWithAppAccount.setApplicationAccount(null);

        when(utilService.getRandomApplicationAccountNumber()).thenReturn("randomGeneratedNumber");

        ApplicationAccount returnedAppAccount = cut.createAccountforUser(userWithAppAccount);

        assertThat(returnedAppAccount.getAccountNumber()).isNotNull().isEqualTo("randomGeneratedNumber");
        assertThat(returnedAppAccount.getBalance()).isEqualTo(0d);
        assertThat(userWithAppAccount.getApplicationAccount()).isEqualTo(returnedAppAccount);
    }

    @Test
    @Order(8)
    void testCreateAccountforUser_whenUserRegistredAndHasAppAcount_thenReturnUserApplicationAccount() {
     
    
        ApplicationAccount returnedAppAccount = cut.createAccountforUser(user);

        assertThat(returnedAppAccount.getAccountNumber()).isNotNull();
        assertThat(returnedAppAccount.getBalance()).isNotEqualTo(0d);
        assertThat(user.getApplicationAccount()).isEqualTo(returnedAppAccount);
    }

    @Test
    @Order(9)
    void testCreateAccountforUser_whenUserIsNull_thenThrowException()  {

        assertThrows(UserNotFoundException.class, () -> {
            cut.createAccountforUser(null);
        });
    }

    private static Stream<Arguments> withdrawArgumentsCorrectResult() {

        return Stream.of(Arguments.of(appAccount, 100d), Arguments.of(appAccount, 900d));
    }


    @ParameterizedTest
    @MethodSource("withdrawArgumentsCorrectResult")
    @Order(10)
    void testWithdraw_whenAppAccountBalanceGreaterThanOrEqualToAmount(ApplicationAccount appAccount, double amount) {
        double expectedResult = appAccount.getBalance() - amount;

        cut.withdraw(appAccount, amount);

        ArgumentCaptor<ApplicationAccount> appAccountCaptor = ArgumentCaptor.forClass(ApplicationAccount.class);
        verify(appAccountRepository, times(1)).save(appAccountCaptor.capture());

        assertThat(appAccountCaptor.getValue().getBalance()).isEqualTo(expectedResult);

    }

    private static Stream<Arguments> withdrawArgumentsThrowException() {

        return Stream.of(Arguments.of(appAccount, 1100d));
    }

    @ParameterizedTest
    @MethodSource("withdrawArgumentsThrowException")
    @Order(11)
    void testWithdraw_whenAppAccountBalanceLessThanAmount_thenTrowException(ApplicationAccount appAccount, double amount) {


        assertThrows(ApplicationAccountException.class, () -> {
            cut.withdraw(appAccount, amount);
        });


        verify(appAccountRepository, never()).save(Mockito.any(ApplicationAccount.class));

    }

    private static Stream<Arguments> ArgumentsForCredit() {

        return Stream.of(Arguments.of(appAccount, 1100d));
    }

    @ParameterizedTest
    @MethodSource("ArgumentsForCredit")
    @Order(12)
    void testCredit(ApplicationAccount appAccount, double amount) {
        appAccount.setBalance(0d);
        amount = 100d;
        double expectedResult = appAccount.getBalance() + amount;

        cut.credit(appAccount, amount);

        ArgumentCaptor<ApplicationAccount> appAccountCaptor = ArgumentCaptor.forClass(ApplicationAccount.class);
        verify(appAccountRepository, times(1)).save(appAccountCaptor.capture());

        assertThat(appAccountCaptor.getValue().getBalance()).isEqualTo(expectedResult);

    }



}
