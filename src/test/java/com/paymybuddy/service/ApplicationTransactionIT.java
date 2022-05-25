package com.paymybuddy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.paymybuddy.exceptions.ApplicationAccountException;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ApplicationAccountRepository;
import com.paymybuddy.repository.ApplicationTransactionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;


@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(OrderAnnotation.class)
public class ApplicationTransactionIT {

    @MockBean
    private ApplicationTransactionRepository appTransactionRepository;

    @MockBean
    private ApplicationAccountRepository appAccountRepository;

    @MockBean
    private ApplicationAccountService appAccountService;

    @Autowired
    private ApplicationTransactionService cut;

    // entities used for testing
    static User sender = new User();
    static User receiver = new User();
    static ApplicationTransaction appTransaction1 = new ApplicationTransaction();
    static ApplicationTransaction appTransaction2 = new ApplicationTransaction();
    static ApplicationAccount senderAccount = new ApplicationAccount();
    static ApplicationAccount receiverAccount = new ApplicationAccount();
    static List<ApplicationTransaction> appTransactions = new ArrayList<>();
    static List<ApplicationTransaction> appSenderTransactions = new ArrayList<>();
    static List<ApplicationTransaction> appReceiverTransactions = new ArrayList<>();
    static Date date1 = new Date();
    static Date date2 = new Date();

    @BeforeAll
    public static void setUp() {
        sender.setLastName("lastNameSender");
        sender.setFirstName("firstNameSender");
        sender.setEmail("sender@gmail.com");
        sender.setApplicationAccount(senderAccount);
        senderAccount.setBalance(1000d);
        senderAccount.setUser(sender);
        senderAccount.setAccountNumber("senderAccountNumber");


        receiver.setLastName("lastNameReciever");
        receiver.setFirstName("firstNameReceiver");
        receiver.setEmail("receiver@gmail.com");
        receiver.setApplicationAccount(receiverAccount);
        receiverAccount.setBalance(1000d);
        receiverAccount.setUser(receiver);
        receiverAccount.setAccountNumber("receiverAccountNumber");

        Calendar cal = Calendar.getInstance();
        cal.set(2022, 05, 21);
        date1.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        date2.getTime();

        appTransaction1.setDescription("transaction1");
        appTransaction1.setSender(sender);
        appTransaction1.setReceiver(receiver);
        appTransaction1.setTransactionDate(date1);
        appTransaction1.setAmount(100d);


        appTransaction2.setDescription("transaction2");
        appTransaction2.setTransactionDate(date2);
        appTransaction2.setSender(sender);
        appTransaction2.setReceiver(receiver);
        appTransaction2.setAmount(200d);
        appTransaction2.setTransactionDate(date2);

        appTransactions.add(appTransaction1);
        appTransactions.add(appTransaction2);

        appSenderTransactions.add(appTransaction1);
        appReceiverTransactions.add(appTransaction2);
    }

    @Test
    @Order(1)
    void findByUSer_whenUserExisted_thenReturnUserTransactions() {

        when(appTransactionRepository.findByUser(Mockito.any(User.class))).thenReturn(appTransactions);

        List<ApplicationTransaction> transactionsForUser = cut.findByUser(sender);

        assertThat(transactionsForUser).containsExactlyInAnyOrder(appTransaction1, appTransaction2);
    }

    @Test
    @Order(2)
    void findByUSer_whenUserNotExisted_thenReturnEmptyList() {

        when(appTransactionRepository.findByUser(Mockito.any(User.class))).thenReturn(new ArrayList<>());

        List<ApplicationTransaction> transactionsForUser = cut.findByUser(sender);

        assertThat(transactionsForUser).isEmpty();
    }

    @Test
    @Order(3)
    void save_whenTransactionExists_thenReturnTransaction() {

        when(appTransactionRepository.save(Mockito.any(ApplicationTransaction.class))).thenReturn(appTransaction1);

        ApplicationTransaction savedTransaction = cut.save(appTransaction1);

        assertThat(savedTransaction).isEqualTo(appTransaction1);
    }

    @Test
    @Order(4)
    void save_whenTransactionNull_thenThrowsException() {

        ApplicationTransaction nullApplicationTransaction = null;

        when(appTransactionRepository.save(null)).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> {
            cut.save(nullApplicationTransaction);
        });
    }

    @Test
    @Order(5)
    void calculateAmountCommission_whenAmountZero_thenReturnZero() {
        double amount = 0;
        double result = cut.calculateAmountCommission(amount);
        assertThat(result).isZero();
    }

    @Test
    @Order(6)
    void calculateAmountCommission() {
        double amount = 10;
        double result = cut.calculateAmountCommission(amount);
        assertThat(result).isEqualTo(10 * ApplicationTransaction.COMMISSIONPERCENT);
    }

    /**
     * Transaction, sender and receiver are by definition not null and tested in controller.
     */
    @Test
    @Order(7)
    void proceedTransaction_whenEveryThingOK_thenCommit() {
        // we mock applicationAccount & applicationTransaction Repository to not save in database
        // not that the returned transaction not corresponding to transactionResult: we don't mind
        // the only aim is to mock transactionRepository
        when(appAccountRepository.save(Mockito.any(ApplicationAccount.class))).thenReturn(new ApplicationAccount());
        when(appTransactionRepository.save(Mockito.any(ApplicationTransaction.class))).thenReturn(new ApplicationTransaction());


        ApplicationTransaction transactionResult = cut.proceed(appTransaction1, sender, receiver);
        assertThat(transactionResult.getTransactionDate()).isAfter(date1);
        assertThat(transactionResult.getSender()).isEqualTo(sender);
        assertThat(transactionResult.getReceiver()).isEqualTo(receiver);
        assertThat(transactionResult.getAmountCommission()).isEqualTo(100d * ApplicationTransaction.COMMISSIONPERCENT);

        ArgumentCaptor<ApplicationAccount> appAccountCaptor = ArgumentCaptor.forClass(ApplicationAccount.class);
        verify(appAccountRepository, times(2)).save(appAccountCaptor.capture());
        verify(appTransactionRepository, times(1)).save(Mockito.any(ApplicationTransaction.class));
        assertThat(appAccountCaptor.getAllValues().get(0).getBalance()).isEqualTo(895d);
        assertThat(appAccountCaptor.getAllValues().get(1).getBalance()).isEqualTo(1100d);
    }

    @Test
    @Order(8)
    void proceedTransaction_whenSenderAccountLessThanTransactionAmount_thenRollBack() {

        // given sender's Account balance =0 => appAccountService throws ApplicationAccountException
        sender.getApplicationAccount().setBalance(0d);

        // when
        assertThrows(ApplicationAccountException.class, () -> {
            cut.proceed(appTransaction1, sender, receiver);
        });

        // then assertion of repository never use save() method cause of rollback
        assertThat(appTransaction1.getTransactionDate()).isEqualToIgnoringSeconds(date1);
        assertThat(appTransaction1.getSender()).isEqualTo(sender);
        assertThat(appTransaction1.getReceiver()).isEqualTo(receiver);
        assertThat(appTransaction1.getAmountCommission()).isEqualTo(appTransaction1.getAmount() * ApplicationTransaction.COMMISSIONPERCENT);


        verify(appAccountRepository, never()).save(Mockito.any(ApplicationAccount.class));
        verify(appTransactionRepository, never()).save(Mockito.any(ApplicationTransaction.class));
        assertThat(sender.getApplicationAccount().getBalance()).isEqualTo(0d);
        assertThat(receiver.getApplicationAccount().getBalance()).isEqualTo(1000d);
    }

    @Test
    @Order(9)
    void proceedTransaction_whenApplicationAccountServiceWithdrawThrowsException_thenRollBack() {

        // given
        doThrow(RuntimeException.class).when(appAccountService).withdraw(Mockito.any(ApplicationAccount.class), Mockito.anyDouble());

        // when
        assertThrows(RuntimeException.class, () -> {
            cut.proceed(appTransaction1, sender, receiver);
        });

        // then assertion of repository never use save() method cause of rollback
        assertThat(appTransaction1.getTransactionDate()).isEqualToIgnoringSeconds(date1);
        assertThat(appTransaction1.getSender()).isEqualTo(sender);
        assertThat(appTransaction1.getReceiver()).isEqualTo(receiver);
        assertThat(appTransaction1.getAmountCommission()).isEqualTo(appTransaction1.getAmount() * ApplicationTransaction.COMMISSIONPERCENT);


        verify(appAccountRepository, never()).save(Mockito.any(ApplicationAccount.class));
        verify(appTransactionRepository, never()).save(Mockito.any(ApplicationTransaction.class));
        assertThat(sender.getApplicationAccount().getBalance()).isEqualTo(1000d);
        assertThat(receiver.getApplicationAccount().getBalance()).isEqualTo(1000d);
    }

    @Test
    @Order(10)
    void proceedTransaction_whenApplicationAccountServiceCreditThrowsException_thenRollBack() {

        // given
        doThrow(RuntimeException.class).when(appAccountService).credit(Mockito.any(ApplicationAccount.class), Mockito.anyDouble());

        // when
        assertThrows(RuntimeException.class, () -> {
            cut.proceed(appTransaction1, sender, receiver);
        });

        // then assertion of repository never use save() method cause of rollback
        assertThat(appTransaction1.getTransactionDate()).isEqualToIgnoringSeconds(date1);
        assertThat(appTransaction1.getSender()).isEqualTo(sender);
        assertThat(appTransaction1.getReceiver()).isEqualTo(receiver);
        assertThat(appTransaction1.getAmountCommission()).isEqualTo(appTransaction1.getAmount() * ApplicationTransaction.COMMISSIONPERCENT);


        verify(appAccountRepository, never()).save(Mockito.any(ApplicationAccount.class));
        verify(appTransactionRepository, never()).save(Mockito.any(ApplicationTransaction.class));
        assertThat(sender.getApplicationAccount().getBalance()).isEqualTo(1000d);
        assertThat(receiver.getApplicationAccount().getBalance()).isEqualTo(1000d);
    }
}
