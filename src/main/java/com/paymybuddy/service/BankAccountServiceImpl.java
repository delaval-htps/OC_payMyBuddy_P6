package com.paymybuddy.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.paymybuddy.exceptions.BankAccountException;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;


@Service
public class BankAccountServiceImpl implements AccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    /**
     * save a BankAccount.
     * 
     * @param bankAccount
     * @return the saved bankAccount if success
     */
    public BankAccount save(BankAccount bankAccount) {
        return bankAccountRepository.save( bankAccount);
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

    public Optional<BankAccount> findById(Long id) {
        return bankAccountRepository.findById(id);
    }

    public Optional<BankAccount> findByEmail(String email) {
        return bankAccountRepository.findByEmail(email);
    }

    @Override
    @Transactional(rollbackFor  = { RuntimeException.class})
    public void withdraw(Account bankAccount, double amount) {
        if (bankAccount.getBalance() >= amount) {

            bankAccount.setBalance(bankAccount.getBalance() - amount);
           
              bankAccountRepository.save( (BankAccount) bankAccount);
      
          } else {
            throw new BankAccountException("You can't send this amount (commision included)" + amount + " to your application account because your bank account's balance is not sufficient");
          }
      
    }

    @Override
    @Transactional(rollbackFor = { RuntimeException.class})
    public void credit(Account bankAccount, double amount) {
        bankAccount.setBalance(bankAccount.getBalance() + amount);

        bankAccountRepository.save((BankAccount)bankAccount);
        
    }
}
