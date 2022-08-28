package com.paymybuddy.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paymybuddy.model.Invoice;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.InvoiceRepository;

@Service
public class InvoiceService {
    
    @Autowired
    private InvoiceRepository invoiceRepository;

    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    public List<Invoice> findAllBySender(User user) {
        return invoiceRepository.findAllBySender(user);
    }

    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }
}
