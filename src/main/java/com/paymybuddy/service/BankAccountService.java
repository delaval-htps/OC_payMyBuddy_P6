package com.paymybuddy.service;

import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    public BankAccount save(BankAccount bankAccount){
        return bankAccountRepository.save(bankAccount);
    }
}
