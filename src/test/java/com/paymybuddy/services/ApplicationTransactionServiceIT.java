package com.paymybuddy.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.db.api.Assertions.assertThat;
import static org.assertj.db.output.Outputs.output;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.List;

import org.assertj.db.type.Changes;
import org.assertj.db.type.DateTimeValue;
import org.assertj.db.type.Source;
import org.assertj.db.type.Table;
import org.assertj.db.type.TimeValue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.paymybuddy.exceptions.ApplicationAccountException;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.ApplicationTransaction.TransactionType;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ApplicationAccountRepository;
import com.paymybuddy.service.AccountService;
import com.paymybuddy.service.ApplicationTransactionService;
import com.paymybuddy.service.InvoiceService;
import com.paymybuddy.service.UserService;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(OrderAnnotation.class)
public class ApplicationTransactionServiceIT {

    @Autowired
    private UserService userService;

    @MockBean
    private ApplicationAccountRepository appAccountRepository;

    @Autowired
    @Qualifier(value = "ApplicationAccountService")
    private AccountService appAccountService;

    @Autowired
    @Qualifier(value = "BankAccountService")
    private AccountService bankAccountService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private ApplicationTransactionService cut;

    private static Source source;
    private static Table transactionTable;
    private static Table invoiceTable;

    @BeforeAll
    public static void setUp() {
        source = new Source("jdbc:h2:mem:testdb", "sa", "");
    }

    @Test
    @Order(1)
    void findBySender_whenUserExisted_thenReturnUserTransactions() {

        List<ApplicationTransaction> transactionsForUser = cut.findBySender(userService.findById(1L).get());

        assertThat(transactionsForUser).hasSize(2);
        assertThat(transactionsForUser.get(0).getId()).isNotNull();
        assertThat(transactionsForUser.get(0).getAmount()).isEqualTo(100d);
        assertThat(transactionsForUser.get(0).getAmountCommission()).isEqualTo(5d);
        assertThat(transactionsForUser.get(0).getSender().getEmail()).isEqualTo("delaval.htps@gmail.com");
        assertThat(transactionsForUser.get(0).getReceiver().getEmail()).isEqualTo("emilie.baudouin@gmail.com");
        assertThat(transactionsForUser.get(1).getId()).isNotNull();
        assertThat(transactionsForUser.get(1).getAmount()).isEqualTo(200d);
        assertThat(transactionsForUser.get(1).getAmountCommission()).isEqualTo(10d);
        assertThat(transactionsForUser.get(1).getSender().getEmail()).isEqualTo("delaval.htps@gmail.com");
        assertThat(transactionsForUser.get(1).getReceiver().getEmail()).isEqualTo("emilie.baudouin@gmail.com");
    }

    @Test
    @Order(2)
    void findBySender_whenNoTransaction_thenReturnEmptyList() {

        List<ApplicationTransaction> transactionsForUser = cut.findBySender(userService.findById(2L).get());

        assertThat(transactionsForUser).isEmpty();
    }

    @Test
    @Order(3)
    void findBySender_whenUserIsNull_thenThrowsException() {
        User newUser = null;
        assertThrows(IllegalArgumentException.class, () -> {
            cut.findBySender(newUser);
        });
    }

    @Test
    @Order(4)
    void save_whenTransactionExists_thenReturnTransaction() {
        ApplicationTransaction mockAppTransaction = new ApplicationTransaction();
        mockAppTransaction.setReceiver(userService.findByEmail("delaval.htps@gmail.com").get());
        mockAppTransaction.setSender(userService.findByEmail("emilie.baudouin@gmail.com").get());
        mockAppTransaction.setAmount(300d);
        mockAppTransaction.setAmountCommission(315d);
        mockAppTransaction.setDescription("transactionTestSave");
        ApplicationTransaction savedTransaction = cut.save(mockAppTransaction);

        assertThat(savedTransaction.getId()).isNotNull();
        assertThat(savedTransaction.getAmount()).isEqualTo(300d);
        assertThat(savedTransaction.getAmountCommission()).isEqualTo(315d);
        assertThat(savedTransaction.getReceiver().getEmail()).isEqualTo("delaval.htps@gmail.com");
        assertThat(savedTransaction.getSender().getEmail()).isEqualTo("emilie.baudouin@gmail.com");
        assertThat(savedTransaction.getDescription()).isEqualTo("transactionTestSave");
    }

    @Test
    @Order(5)
    void save_whenTransactionNull_thenThrowsException() {

        ApplicationTransaction nullApplicationTransaction = null;

        assertThrows(IllegalArgumentException.class, () -> {
            cut.save(nullApplicationTransaction);
        });
    }

    @Test
    @Order(6)
    void calculateAmountCommission_whenAmountGreaterThanZero_thenReturnOneCent() {
        double amount = 0.01d;
        double result = cut.calculateAmountCommission(amount);
        assertThat(result).isEqualTo(0.01d);
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
    void calculateAmountCommission_whenAmountNegative_thenThrowException() {
        double amount = -Double.MIN_VALUE;

        assertThrows(IllegalArgumentException.class, () -> {
            cut.calculateAmountCommission(amount);
        });
    }


    @Test
    void proceedTransactionBetweenUsers_whenEveryThingOK_thenCommit() throws ParseException {
        ApplicationTransaction mockAppTransaction = new ApplicationTransaction();
        // given: we create a transaction between userId 1 and userId2
        mockAppTransaction.setReceiver(userService.findByEmail("delaval.htps@gmail.com").get());
        mockAppTransaction.setSender(userService.findByEmail("emilie.baudouin@gmail.com").get());
        mockAppTransaction.setAmount(300d);
        mockAppTransaction.setDescription("transactionTestSave");

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");

        Changes transactionChanges = new Changes(transactionTable);
        Changes invoiceChanges = new Changes(invoiceTable);

        transactionChanges.setStartPointNow();
        invoiceChanges.setStartPointNow();
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // when: we proceed transaction
        cut.proceedTransactionBetweenUsers(mockAppTransaction,
                userService.findByEmail("delaval.htps@gmail.com").get(),
                userService.findByEmail("emilie.baudouin@gmail.com").get());

        transactionChanges.setEndPointNow();
        invoiceChanges.setEndPointNow();

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // then assertion of creation of new row with our new transaction
        assertThat(transactionChanges).hasNumberOfChanges(1);
        assertThat(invoiceChanges).hasNumberOfChanges(1);

        assertThat(transactionChanges).change()
                .rowAtStartPoint().doesNotExist()
                .rowAtEndPoint().exists().hasNumberOfColumns(8);

        assertThat(invoiceChanges).change()
                .rowAtStartPoint().doesNotExist()
                .rowAtEndPoint().exists().hasNumberOfColumns(5);

        assertThat(transactionChanges).change()
                .rowAtEndPoint()
                .value("id").isNotNull()
                .value("amount").isEqualTo(300)
                .value("amount_commission").isEqualTo(15)
                .value("description").isEqualTo("transactionTestSave")
                .value("transaction_date").isCloseTo(DateTimeValue.now(), TimeValue.of(1, 0))
                .value("sender_id").isEqualTo(1)
                .value("receiver_id").isEqualTo(2);

        assertThat(invoiceChanges).change()
                .rowAtEndPoint()
                .value("id").isNotNull()
                .value("price_ht").isEqualTo(315)
                .value("price_ttc").isEqualTo(378)
                .value("transaction_id").isEqualTo(3L)
                .value("date_invoice").isCloseTo(DateTimeValue.now(), TimeValue.of(1, 0));

    }

    @Test
    void proceedTransactionBetweenUsers_whenSenderAccountLessThanTransactionAmount_thenRollBack() {

        // given: we create a transaction between userId 1 and userId2
        // sender's Account balance < transaction's' amount => appAccountService throws
        // ApplicationAccountException
        ApplicationTransaction mockAppTransaction = new ApplicationTransaction();
        mockAppTransaction.setReceiver(userService.findByEmail("delaval.htps@gmail.com").get());
        mockAppTransaction.setSender(userService.findByEmail("emilie.baudouin@gmail.com").get());
        mockAppTransaction.setAmount(1000.01d);
        mockAppTransaction.setDescription("transctionRollBack");

        User sender = userService.findByEmail("delaval.htps@gmail.com").get();
        User receiver = userService.findByEmail("emilie.baudouin@gmail.com").get();

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");

        Changes transactionChanges = new Changes(transactionTable);
        Changes invoiceChanges = new Changes(invoiceTable);

        transactionChanges.setStartPointNow();
        invoiceChanges.setStartPointNow();
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        transactionTable = new Table(source, "transaction");
        invoiceChanges.setStartPointNow();
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // when
        assertThrows(ApplicationAccountException.class, () -> {
            cut.proceedTransactionBetweenUsers(mockAppTransaction, sender, receiver);
        });

        transactionChanges.setEndPointNow();
        invoiceChanges.setEndPointNow();

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // then assertion of creation of new row with our new transaction
        assertThat(transactionChanges).hasNumberOfChanges(0);
        assertThat(invoiceChanges).hasNumberOfChanges(0);

    }

    @Test
    void proceedTransactionBetweenUsers_whenApplicationAccountThrowsIllegalArgumentException_thenRollBack() {

        // given: we create a transaction between userId 1 and userId2
        // sender's Account balance < transaction's' amount => appAccountService throws
        // ApplicationAccountException
        ApplicationTransaction mockAppTransaction = new ApplicationTransaction();
        mockAppTransaction.setReceiver(userService.findByEmail("delaval.htps@gmail.com").get());
        mockAppTransaction.setSender(userService.findByEmail("emilie.baudouin@gmail.com").get());
        mockAppTransaction.setAmount(1000.01d);
        mockAppTransaction.setDescription("transctionRollBack");

        User sender = userService.findByEmail("delaval.htps@gmail.com").get();
        User receiver = userService.findByEmail("emilie.baudouin@gmail.com").get();

        when(appAccountRepository.save(Mockito.any(ApplicationAccount.class)))
                .thenThrow(IllegalArgumentException.class);

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");

        Changes transactionChanges = new Changes(transactionTable);
        Changes invoiceChanges = new Changes(invoiceTable);

        transactionChanges.setStartPointNow();
        invoiceChanges.setStartPointNow();
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        transactionTable = new Table(source, "transaction");
        invoiceChanges.setStartPointNow();
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // when
        assertThrows(ApplicationAccountException.class, () -> {
            cut.proceedTransactionBetweenUsers(mockAppTransaction, sender, receiver);
        });

        transactionChanges.setEndPointNow();
        invoiceChanges.setEndPointNow();

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // then assertion of creation of new row with our new transaction
        assertThat(transactionChanges).hasNumberOfChanges(0);
        assertThat(invoiceChanges).hasNumberOfChanges(0);

    }

    @Test
    void proceedBankTransaction_whenWithdrawEveryThingOK_thenCommit() throws ParseException {
        ApplicationTransaction mockAppTransaction = new ApplicationTransaction();
        // given: we create a transaction between userId 1 and userId2
        mockAppTransaction.setReceiver(userService.findByEmail("delaval.htps@gmail.com").get());
        mockAppTransaction.setSender(userService.findByEmail("delaval.htps@gmail.com").get());
        mockAppTransaction.setAmount(300d);
        mockAppTransaction.setDescription("bank-transaction");
        mockAppTransaction.setType(TransactionType.WITHDRAW);

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");

        Changes transactionChanges = new Changes(transactionTable);
        Changes invoiceChanges = new Changes(invoiceTable);

        transactionChanges.setStartPointNow();
        invoiceChanges.setStartPointNow();
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // when: we proceed transaction
        cut.proceedBankTransaction(mockAppTransaction,
                userService.findByEmail("delaval.htps@gmail.com").get());

        transactionChanges.setEndPointNow();
        invoiceChanges.setEndPointNow();

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // then assertion of creation of new row with our new transaction
        assertThat(transactionChanges).hasNumberOfChanges(1);
        assertThat(invoiceChanges).hasNumberOfChanges(1);

        assertThat(transactionChanges).change()
                .rowAtStartPoint().doesNotExist()
                .rowAtEndPoint().exists().hasNumberOfColumns(8);

        assertThat(invoiceChanges).change()
                .rowAtStartPoint().doesNotExist()
                .rowAtEndPoint().exists().hasNumberOfColumns(5);

        assertThat(transactionChanges).change()
                .rowAtEndPoint()
                .value("id").isNotNull()
                .value("amount").isEqualTo(300)
                .value("amount_commission").isEqualTo(15)
                .value("description").isEqualTo("bank-transaction")
                .value("transaction_date").isCloseTo(DateTimeValue.now(), TimeValue.of(1, 0))
                .value("sender_id").isEqualTo(1)
                .value("receiver_id").isEqualTo(1)
                .value("type").isEqualTo(TransactionType.WITHDRAW.toString());

        assertThat(invoiceChanges).change()
                .rowAtEndPoint()
                .value("id").isNotNull()
                .value("price_ht").isEqualTo(315)
                .value("price_ttc").isEqualTo(378)
                .value("transaction_id").isEqualTo(3L)
                .value("date_invoice").isCloseTo(DateTimeValue.now(), TimeValue.of(1, 0));

    }

    @Test
    void proceedBankTransaction_whenCreditEveryThingOK_thenCommit() throws ParseException {
        ApplicationTransaction mockAppTransaction = new ApplicationTransaction();
        // given: we create a transaction between userId 1 and userId2
        mockAppTransaction.setReceiver(userService.findByEmail("delaval.htps@gmail.com").get());
        mockAppTransaction.setSender(userService.findByEmail("delaval.htps@gmail.com").get());
        mockAppTransaction.setAmount(300d);
        mockAppTransaction.setDescription("bank-transaction");
        mockAppTransaction.setType(TransactionType.CREDIT);

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");

        Changes transactionChanges = new Changes(transactionTable);
        Changes invoiceChanges = new Changes(invoiceTable);

        transactionChanges.setStartPointNow();
        invoiceChanges.setStartPointNow();
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // when: we proceed transaction
        cut.proceedBankTransaction(mockAppTransaction,
                userService.findByEmail("delaval.htps@gmail.com").get());

        transactionChanges.setEndPointNow();
        invoiceChanges.setEndPointNow();

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // then assertion of creation of new row with our new transaction
        assertThat(transactionChanges).hasNumberOfChanges(1);
        assertThat(invoiceChanges).hasNumberOfChanges(1);

        assertThat(transactionChanges).change()
                .rowAtStartPoint().doesNotExist()
                .rowAtEndPoint().exists().hasNumberOfColumns(8);

        assertThat(invoiceChanges).change()
                .rowAtStartPoint().doesNotExist()
                .rowAtEndPoint().exists().hasNumberOfColumns(5);

        assertThat(transactionChanges).change()
                .rowAtEndPoint()
                .value("id").isNotNull()
                .value("amount").isEqualTo(300)
                .value("amount_commission").isEqualTo(15)
                .value("description").isEqualTo("bank-transaction")
                .value("transaction_date").isCloseTo(DateTimeValue.now(), TimeValue.of(1, 0))
                .value("sender_id").isEqualTo(1)
                .value("receiver_id").isEqualTo(1)
                .value("type").isEqualTo(TransactionType.CREDIT.toString());

        assertThat(invoiceChanges).change()
                .rowAtEndPoint()
                .value("id").isNotNull()
                .value("price_ht").isEqualTo(315)
                .value("price_ttc").isEqualTo(378)
                .value("transaction_id").isEqualTo(3L)
                .value("date_invoice").isCloseTo(DateTimeValue.now(), TimeValue.of(1, 0));

    }

    @Test
    void proceedBankTransaction_whenWithdrawAndApplicationAccountLessThanAmount_thenRollBack() {

        // given: we create a transaction between userId 1 and userId2
        // sender's Account balance < transaction's' amount => appAccountService throws
        // ApplicationAccountException
        ApplicationTransaction mockAppTransaction = new ApplicationTransaction();
        mockAppTransaction.setReceiver(userService.findByEmail("delaval.htps@gmail.com").get());
        mockAppTransaction.setAmount(2000d);
        mockAppTransaction.setDescription("transctionRollBack");
        mockAppTransaction.setType(TransactionType.WITHDRAW);

        User sender = userService.findByEmail("delaval.htps@gmail.com").get();

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");

        Changes transactionChanges = new Changes(transactionTable);
        Changes invoiceChanges = new Changes(invoiceTable);

        transactionChanges.setStartPointNow();
        invoiceChanges.setStartPointNow();
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        transactionTable = new Table(source, "transaction");
        invoiceChanges.setStartPointNow();
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // when
        assertThrows(ApplicationAccountException.class, () -> {
            cut.proceedBankTransaction(mockAppTransaction, sender);
        });

        transactionChanges.setEndPointNow();
        invoiceChanges.setEndPointNow();

        transactionTable = new Table(source, "transaction");
        invoiceTable = new Table(source, "invoice");
        output(transactionTable).toConsole();
        output(invoiceTable).toConsole();

        // then assertion of creation of new row with our new transaction
        assertThat(transactionChanges).hasNumberOfChanges(0);
        assertThat(invoiceChanges).hasNumberOfChanges(0);

    }
   
}
