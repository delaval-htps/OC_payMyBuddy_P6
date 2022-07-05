package com.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import java.util.Optional;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class BankAccountServiceImplTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountServiceImpl cut;

    @Test
    @Order(1)
    void saveBankAccount_whenBankAccountExists_thenReturnBankAccount() {

        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban("iban_test");
        bankAccount.setBalance(1000d);

        when(bankAccountRepository.save(Mockito.any(BankAccount.class))).thenReturn(bankAccount);

        Account savedBankAccount = cut.save(bankAccount);

        assertThat(savedBankAccount).isEqualTo(bankAccount);
    }

    @Test
    @Order(2)
    void saveBankAccount_whenBankAccountNull_thenReturnBankAccount() {

        BankAccount bankAccount = null;

        when(bankAccountRepository.save(null)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> {
            cut.save(bankAccount);
        });
    }

    @Test
    @Order(3)
    void findByIban_whenIbanExisted_thenReturnLinkedBankAccount() {

        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000d);
        bankAccount.setIban("numberOfIban");

        when(bankAccountRepository.findByIban(Mockito.anyString())).thenReturn(Optional.of(bankAccount));

        Optional<BankAccount> savedBankAccount = cut.findByIban("numberOfIban");

        assertThat(savedBankAccount.get()).isNotNull();
        assertThat(savedBankAccount).contains(bankAccount);
    }

    @Test
    @Order(4)
    void findByIban_whenIbanNotExisted_thenReturnEmptyOptional() {

        when(bankAccountRepository.findByIban(Mockito.anyString())).thenReturn(Optional.empty());

        Optional<BankAccount> savedBankAccount = cut.findByIban("numberOfIbanNotExisted");

        assertThat(savedBankAccount).isEmpty();
    }


}
