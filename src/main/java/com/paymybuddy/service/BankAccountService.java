package com.paymybuddy.service;

import java.util.Optional;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    /**
     * save a BankAccount.
     * 
     * @param bankAccount
     * @return the saved bankAccount if success
     */
    public BankAccount save(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    /**
     * find a bankAccount by it's number of Iban.
     * 
     * @param iban number of Iban of BankAccount (type String)
     * @return Optional of bankAccount
     */
    public Optional<BankAccount> findByIban(String iban) {
        return bankAccountRepository.findByIban(iban);
    }
}
