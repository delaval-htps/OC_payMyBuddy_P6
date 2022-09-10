package com.paymybuddy.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.paymybuddy.exceptions.ApplicationTransactionException;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.ApplicationTransaction.TransactionType;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.model.Invoice;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ApplicationTransactionRepository;
import com.paymybuddy.repository.InvoiceRepository;
import com.paymybuddy.service.InvoiceService;

@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ApplicationTransactionRepository appTransactionRepository;

    @InjectMocks
    private InvoiceService cut;

    static User sender = new User();
    static User receiver = new User();
    static ApplicationTransaction mockTransaction = new ApplicationTransaction();
    static ApplicationAccount senderAccount = new ApplicationAccount();
    static ApplicationAccount receiverAccount = new ApplicationAccount();
    static Date date1 = new Date();
    static Date date2 = new Date();
    static BankAccount bankAccount = new BankAccount();
    static ApplicationTransaction bankTransaction = new ApplicationTransaction();

    static Invoice mockInvoice = new Invoice();
    static List<Invoice> invoices = new ArrayList<>();

    @BeforeEach
    public void init() {
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

        mockTransaction.setId(1L);
        mockTransaction.setDescription("transaction1");
        mockTransaction.setSender(sender);
        mockTransaction.setReceiver(receiver);
        mockTransaction.setType(TransactionType.WITHDRAW);
        mockTransaction.setTransactionDate(date1);
        mockTransaction.setAmount(100d);
        mockTransaction.setAmountCommission(5);
        bankAccount.setBalance(1000d);
        bankAccount.setIban("1234-1234-1234-1324-1234-1234-1234-1234-12");
        bankAccount.setBic("TESTACOS");

        mockInvoice.setDateInvoice(new Date());
        mockInvoice.setPrice(100d);
        mockInvoice.setTransaction(mockTransaction);

        invoices.add(mockInvoice);
    }

    @Test
    void findById_whenInvoiceExisted() {
        when(invoiceRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(mockInvoice));

        assertThat(cut.findById(1L)).contains(mockInvoice);
    }

    @Test
    void findById_whenInvoiceNotExisted() {
        when(invoiceRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThat(cut.findById(1L)).isEmpty();
    }

    @Test
    void findByUser_whenInvoiceExisted() {

        when(invoiceRepository.findAllBySender(Mockito.any(User.class))).thenReturn(invoices);

        assertThat(cut.findAllBySender(sender)).contains(mockInvoice);
    }

    @Test
    void findByUser_whenInvoiceNotExisted() {
        when(invoiceRepository.findAllBySender(Mockito.any(User.class))).thenReturn(new ArrayList<>());

        assertThat(cut.findAllBySender(sender)).isEmpty();
    }

    @Test
    void saveInvoice_whenInvoiceExisted() {

        when(invoiceRepository.save(Mockito.any(Invoice.class))).thenReturn(mockInvoice);

        assertThat(cut.save(mockInvoice)).isEqualTo(mockInvoice);
    }

    @Test
    void saveInvoice_whenInvoiceNull() {

        when(invoiceRepository.save(Mockito.any(Invoice.class))).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> {
            cut.save(mockInvoice);
        });
    }

    @Test
    void createInvoiceForTransaction_whenTransactionNull_thenThrowException() {

        ApplicationTransaction mockTransaction = null;

        assertThrows(ApplicationTransactionException.class, () -> {
            cut.createInvoiceForTransaction(mockTransaction);
        }, "Transaction is not found!");
    }

    @Test
    void createInvoiceForTransaction_whenTransactionNotSaved_thenThrowException() {
        mockTransaction.setId(null);
        assertThrows(ApplicationTransactionException.class, () -> {
            cut.createInvoiceForTransaction(mockTransaction);
        }, "Transaction is not found!");
    }

    @Test
    void createInvoiceForTransaction_whenTransactionNotExisted_thenThrowException() {

        when(appTransactionRepository.findById(Mockito.anyLong())).thenReturn(java.util.Optional.empty());

        assertThrows(ApplicationTransactionException.class, () -> {
            cut.createInvoiceForTransaction(mockTransaction);
        }, "the transaction was not registred in application!");
    }

    @Test
    void createInvoiceForTransaction_whenTransactionExisted_thenThrowException() {

        when(appTransactionRepository.findById(Mockito.anyLong())).thenReturn(java.util.Optional.of(mockTransaction));

        cut.createInvoiceForTransaction(mockTransaction);
       
        assertThat(mockTransaction.getInvoice()).isNotNull();
        assertThat(mockTransaction.getInvoice().getPrice()).isEqualTo(105d);
    }
}
