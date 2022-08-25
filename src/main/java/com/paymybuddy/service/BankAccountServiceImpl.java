package com.paymybuddy.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.exceptions.BankAccountException;
import com.paymybuddy.model.Account;
import com.paymybuddy.model.BankAccount;
import com.paymybuddy.repository.BankAccountRepository;


@Component(value = "BankAccountService")
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
       
        if (connectionAndCheckBankAccount(bankAccount)) {

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
        bankAccountRepository.save((BankAccount) bankAccount);
    }
    
    /**
     * method to connect to bank Account of user using IBAN & SWIFT code(BIC)
     * and to check i fbalance of bankAccount >= amount.
     * @param bankAccount user bank Account {@link BankAccount}
     * @return  always true cause we mock this behaviour
     */
    public boolean connectionAndCheckBankAccount(Account bankAccount) {
        return true;

    }
}
