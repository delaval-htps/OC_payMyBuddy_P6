package com.paymybuddy.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ApplicationTransactionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
public class ApplicationTransactionServiceTest {

    @Mock
    private ApplicationTransactionRepository appTransactionRepository;

    @Mock
    private AccountService appAccountService;

    @InjectMocks
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


        receiver.setLastName("lastNameReciever");
        receiver.setFirstName("firstNameReceiver");
        receiver.setEmail("receiver@gmail.com");
        receiver.setApplicationAccount(receiverAccount);
        receiverAccount.setBalance(1000d);
        receiverAccount.setUser(receiver);

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
    void findBySender_whenUserExisted_thenReturnUserTransaction() {

        when(appTransactionRepository.findBySender(Mockito.any(User.class))).thenReturn(appTransactions);

        List<ApplicationTransaction> returnedTransactions = cut.findBySender(sender);

        assertThat(returnedTransactions).containsExactlyInAnyOrder(appTransaction1, appTransaction2);
    }

    @Test
    @Order(2)
    void findBySender_whenUserNotExisted_thenReturnEmptyList() {

        when(appTransactionRepository.findBySender(Mockito.any(User.class))).thenReturn(new ArrayList<>());

        List<ApplicationTransaction> returnedTransactions = cut.findBySender(sender);

        assertThat(returnedTransactions).isEmpty();
    }

    @Test
    @Order(3)
    void findBySender_whenUserNull_thenReturnEmptyList() {

        assertThrows(IllegalArgumentException.class, () -> {
            cut.findBySender(null);
        });
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

        assertThrows(IllegalArgumentException.class, () -> {
            cut.save(nullApplicationTransaction);
        });
    }



    @Test
    @Order(5)
    void calculateAmountCommission_whenAmountZero_thenThrowIllegalArgumentException() {
        double amount = 0;
        assertThrows(IllegalArgumentException.class, () -> {
            cut.calculateAmountCommission(amount);
        });
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.01d,2d})
    @Order(6)
    void calculateAmountCommission_whenAmountBetweenMinAndTwo_thenReturnMinimumCommission(double
    amount) {
        
        double result = cut.calculateAmountCommission(amount);
        assertThat(result).isEqualTo(0.01d);
    }


    @Test
    @Order(6)
    void calculateAmountCommission_whenAmountGreaterThanTwo_thenReturnRoundingHalfUpDoubleValue() {
        double amount = 2.01d;

        double exactResult = cut.calculateAmountCommission(amount);

        BigDecimal expectedResult = BigDecimal.valueOf(amount * ApplicationTransaction.COMMISSIONPERCENT).setScale(2, RoundingMode.HALF_UP);

        assertThat(exactResult).isEqualTo(expectedResult.doubleValue());
    }
    
    @Test
    @Order(7)
    void calculateAmountCommission() {
        double amount = 10;
        double result = cut.calculateAmountCommission(amount);
        assertThat(result).isEqualTo(10 * ApplicationTransaction.COMMISSIONPERCENT);
    }


    @Test
    @Order(8)
    void proceedTransactionTest() {

        ApplicationTransaction transaction = cut.proceedBetweenUsers(appTransaction1, sender, receiver);
        assertThat(transaction.getTransactionDate()).isAfter(date1);
        assertThat(transaction.getSender()).isEqualTo(sender);
        assertThat(transaction.getReceiver()).isEqualTo(receiver);
        assertThat(transaction.getAmountCommission()).isEqualTo(100d * ApplicationTransaction.COMMISSIONPERCENT);

        ArgumentCaptor<ApplicationAccount> appAccountCaptor = ArgumentCaptor.forClass(ApplicationAccount.class);
        verify(appAccountService, times(1)).withdraw(appAccountCaptor.capture(), Mockito.anyDouble());
        assertThat(appAccountCaptor.getValue().getUser()).isEqualTo(sender);
        assertThat(appAccountCaptor.getValue().getBalance()).isEqualTo(1000d);

        verify(appAccountService, times(1)).credit(appAccountCaptor.capture(), Mockito.anyDouble());
        assertThat(appAccountCaptor.getValue().getUser()).isEqualTo(receiver);
        assertThat(appAccountCaptor.getValue().getBalance()).isEqualTo(1000d);
    }
}
