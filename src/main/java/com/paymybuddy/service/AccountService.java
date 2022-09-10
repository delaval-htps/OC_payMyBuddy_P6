package com.paymybuddy.service;

import java.util.UUID;

import com.paymybuddy.model.Account;

/**
 * interface to define withdraw and credit for an any account
 */
public interface AccountService {

    public void withdraw(Account senderAccount, double amount);

    public void credit(Account receiverAccount, double amount);
    
    public static String getRandomApplicationAccountNumber() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
