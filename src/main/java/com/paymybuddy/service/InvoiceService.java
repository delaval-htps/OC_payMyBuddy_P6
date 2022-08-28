package com.paymybuddy.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.exceptions.ApplicationTransactionException;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.Invoice;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ApplicationTransactionRepository;
import com.paymybuddy.repository.InvoiceRepository;

/**
 * Class that contains services for a invoice.
 */
@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ApplicationTransactionRepository appTransactionRepository;

    /**
     * return the invoice by id given.
     * 
     * @param id the given id of invoice to research
     * @return Optional invoice research.
     */
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    /**
     * return all invoices of a user connected.
     * 
     * @param user the sender of all transactions.
     * @return a list of all transactions of user.
     */
    public List<Invoice> findAllBySender(User user) {
        return invoiceRepository.findAllBySender(user);
    }

    /**
     * save a invoice in bdd
     * 
     * @param invoice invoice to save
     * @return return the invoice saved.
     */
    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    /**
     * Create a invoice for transaction given in parameter.
     * transaction must not be null and existed.
     * 
     * @param transaction transaction to create a invoice.
     * @return the invoice created
     */
    public Invoice createInvoiceForTransaction(ApplicationTransaction transaction) {

        Invoice invoiceCreated = new Invoice();
        if (transaction == null || transaction.getId() == null) {
            throw new ApplicationTransactionException("Transaction is not found!");
        }

        Optional<ApplicationTransaction> existedTransaction = appTransactionRepository.findById(transaction.getId());

        if (!existedTransaction.isEmpty()) {
            ApplicationTransaction tempTransaction = existedTransaction.get();

            invoiceCreated.setDateInvoice(new Date());
            invoiceCreated.setPriceHt(transaction.getAmount()+ transaction.getAmountCommission());
            invoiceCreated.setPriceTtc(invoiceCreated.getPriceHt() * Invoice.TAXE_PERCENT);
            invoiceCreated.setTransaction(tempTransaction);

            tempTransaction.setInvoice(invoiceCreated);

            // invoice is created because of cascadeType.persist
            appTransactionRepository.save(tempTransaction);

        } else {
            throw new ApplicationTransactionException("the transaction was not registred in application!");
        }
        return invoiceCreated;
    }
}
