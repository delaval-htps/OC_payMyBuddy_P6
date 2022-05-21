package com.paymybuddy.service;

import java.util.Date;
import java.util.List;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ApplicationTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationTransactionService {

    @Autowired
    private ApplicationTransactionRepository appTransactionRepository;

    @Autowired
    private ApplicationAccountService appAccountService;

    /**
     * return application transactions of a user by his id.
     * 
     * @param id id of user.
     * @return list of user's application transactions
     */
    public List<ApplicationTransaction> findByUser(User user) {
        return appTransactionRepository.findByUser(user);
    }

    /**
     * retrieve all transactions for given sender (sender)
     * 
     * @param sender the user that send money to another one
     * @return all transaction sending by user(sender)
     */
    public List<ApplicationTransaction> findBySender(User sender) {
        return appTransactionRepository.findBySender(sender);
    }

    /**
     * retrieve all transactions for given receiver
     * 
     * @param sender the user that send money to another one
     * @return all transaction receiving by user(receiver)
     */
    public List<ApplicationTransaction> findByReceiver(User receiver) {
        return appTransactionRepository.findByReceiver(receiver);
    }

    /**
     * save a application transaction.
     * 
     * @param AppplicationTransaction transaction to save
     * @return the saved transaction.
     */
    public ApplicationTransaction save(ApplicationTransaction transaction) {
        return appTransactionRepository.save(transaction);
    }

    /**
     * calculate the total withdraw amount of a transaction: amount +commission.
     * 
     * @param amount the amount to withdraw.
     * @return the total to withdraw.
     */
    public double calculateAmountCommission(double amount) {
        return amount * ApplicationTransaction.COMMISSIONPERCENT;
    }

    /**
     * proceed and create and save a transaction between sender and receiver. the application accounts
     * of user will be credited/withdrawed with amount of saved transaction. Arguments of method are not
     * null because verified by controller before.
     * 
     * @param transaction application transaction with amount to send , commission's amount, description
     *        and date.
     * @param sender user that send the amount of transaction and have to pay commission.
     * @param receiver the user that receive amount of transaction.
     * @return the transaction saved with all updated field.
     */
    @Transactional()
    public ApplicationTransaction proceed(ApplicationTransaction transaction, User sender, User receiver) {
        transaction.setTransactionDate(new Date());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmountCommission(this.calculateAmountCommission(transaction.getAmount()));
        appAccountService.withdraw(transaction.getSender().getApplicationAccount(), (transaction.getAmount() + transaction.getAmountCommission()));
        appAccountService.credit(transaction.getReceiver().getApplicationAccount(), transaction.getAmount());

        this.save(transaction);
        return transaction;
    }


}
