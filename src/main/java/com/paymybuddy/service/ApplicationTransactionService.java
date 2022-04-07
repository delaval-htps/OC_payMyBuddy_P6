package com.paymybuddy.service;

import java.util.List;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.repository.ApplicationTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationTransactionService {

    @Autowired
    private ApplicationTransactionRepository appTransactionRepository;

    public List<ApplicationTransaction> findByUserId(Long id) {
        return appTransactionRepository.findByUserId(id);
    }

    public ApplicationTransaction save(ApplicationTransaction transaction) {
        return appTransactionRepository.save(transaction);
    }

    public double calculateAmountCommission(int amount) {
        return Math.abs(amount) * ApplicationTransaction.COMMISSIONPERCENT;
    }

    public void updateUserApplicationAccountFollowingTransaction(int amount, double amountCommission, ApplicationAccount applicationAccount) {
        // TODO for the next time !
    }

}
