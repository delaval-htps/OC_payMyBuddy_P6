package com.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import java.util.stream.Stream;

import com.paymybuddy.exceptions.BankAccountException;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountServiceImpl cut;

    private static BankAccount bankAccount = new BankAccount();

    @BeforeEach
    public void init() {

        bankAccount.setIban("TESTACOS");
        bankAccount.setBalance(1000d);
        bankAccount.setId(1L);
    }

    @Test

    void saveBankAccount_whenBankAccountExists_thenReturnBankAccount() {

        when(bankAccountRepository.save(Mockito.any(BankAccount.class))).thenReturn(bankAccount);

        Account savedBankAccount = cut.save(bankAccount);

        assertThat(savedBankAccount).isEqualTo(bankAccount);
    }

    @Test

    void saveBankAccount_whenBankAccountNull_thenReturnBankAccount() {

        BankAccount bankAccount = null;

        when(bankAccountRepository.save(null)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> {
            cut.save(bankAccount);
        });
    }

    @Test

    void findByIban_whenIbanExisted_thenReturnLinkedBankAccount() {

        when(bankAccountRepository.findByIban(Mockito.anyString())).thenReturn(Optional.of(bankAccount));

        Optional<BankAccount> existedBankAccount = cut.findByIban("TESTACOS");

        assertThat(existedBankAccount.get()).isNotNull();
        assertThat(existedBankAccount).contains(bankAccount);
    }

    @Test
    void findByIban_whenIbanNotExisted_thenReturnEmptyOptional() {

        when(bankAccountRepository.findByIban(Mockito.anyString())).thenReturn(Optional.empty());

        Optional<BankAccount> existedBankAccount = cut.findByIban("numberOfIbanNotExisted");

        assertThat(existedBankAccount).isEmpty();
    }

    @Test
    void findById_whenIdExisted_thenReturnLinkedBankAccount() {

        when(bankAccountRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(bankAccount));

        Optional<BankAccount> existedBankAccount = cut.findById(1L);

        assertThat(existedBankAccount.get()).isNotNull();
        assertThat(existedBankAccount).contains(bankAccount);
    }

    @Test
    void findByEmail_whenEmailNotExisted_thenReturnEmptyOptional() {

        when(bankAccountRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Optional<BankAccount> existedBankAccount = cut.findByEmail("test@gmail.com");

        assertThat(existedBankAccount).isEmpty();
    }
    @Test
    void findByEmail_whenIEmailExisted_thenReturnLinkedBankAccount() {

        when(bankAccountRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(bankAccount));

        Optional<BankAccount> existedBankAccount = cut.findByEmail("test@gmail.com");

        assertThat(existedBankAccount.get()).isNotNull();
        assertThat(existedBankAccount).contains(bankAccount);
    }

    @Test
    void findById_whenIdNotExisted_thenReturnEmptyOptional() {

        when(bankAccountRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Optional<BankAccount> existedBankAccount = cut.findById(2L);

        assertThat(existedBankAccount).isEmpty();
    }


    private static Stream<Arguments> withdrawArgumentsCorrectResult() {
        return Stream.of(
                Arguments.of(bankAccount, 100d),
                Arguments.of(bankAccount, 900d));
    }

    @ParameterizedTest
    @MethodSource("withdrawArgumentsCorrectResult")
    void testWithdraw_whenBankAccountBalanceGreaterThanOrEqualToAmount(BankAccount bankAccount, double amount) {
        double expectedResult = bankAccount.getBalance() - amount;

        cut.withdraw(bankAccount, amount);

        ArgumentCaptor<BankAccount> bankAccountCaptor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository, times(1)).save(bankAccountCaptor.capture());

        assertThat(bankAccountCaptor.getValue().getBalance()).isEqualTo(expectedResult);

    }

    @Test
    void credit_thenSaveBankAccountWithNewBalance() {
        double amount = 100d;
        double expectedResult = bankAccount.getBalance() + amount;
        cut.credit(bankAccount, amount);
        assertThat(bankAccount.getBalance()).isEqualTo(expectedResult);
    }
    
 
}


