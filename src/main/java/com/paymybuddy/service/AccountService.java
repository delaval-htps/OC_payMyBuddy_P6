package com.paymybuddy.service;

import com.paymybuddy.model.Account;

public interface AccountService {

    public void withdraw(Account senderAccount, double amount);

    public void credit(Account receiverAccount, double amount);
}
