package com.paymybuddy.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.paymybuddy.exceptions.UserNotFoundException;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.ApplicationTransaction.TransactionType;
import com.paymybuddy.model.User;
import com.paymybuddy.pagination.Paged;
import com.paymybuddy.pagination.Paging;
import com.paymybuddy.repository.ApplicationTransactionRepository;

@Service
public class ApplicationTransactionService {

    @Autowired
    private ApplicationTransactionRepository appTransactionRepository;

    @Autowired
    private ApplicationAccountServiceImpl appAccountService;

    @Autowired
    private BankAccountServiceImpl bankAccountService;

    /**
     * return application transactions of a user by his id. user must not be null
     * 
     * @param user i user.
     * @return list of user's application transactions
     */
    public List<ApplicationTransaction> findBySender(User user) {
        if (user != null) {
            return appTransactionRepository.findBySender(user);
        } else
            throw new IllegalArgumentException("In method " + this.getClass().getName() + "."
                    + this.getClass().getEnclosingMethod() + "() , user must be not null");
    }

    public Paged<ApplicationTransaction> getPageOfTransaction(User user, int pageNumber, int size) {
        if (user != null) {
            
            Page<ApplicationTransaction> appTransactionsPage = appTransactionRepository.findAllBySender(user,
            PageRequest.of(pageNumber, size, Direction.ASC, "id"));
            
            return new Paged<>(appTransactionsPage, Paging.of(appTransactionsPage.getTotalPages(), pageNumber, size));
            
        } else {
            throw new UserNotFoundException("We can provide the list of transaction because user is not found.");
        }
    }

    /**
     * save a application transaction.
     * 
     * @param transaction transaction to save
     * @return the saved transaction.
     */
    public ApplicationTransaction save(ApplicationTransaction transaction) {
        if (transaction != null) {
            return appTransactionRepository.save(transaction);
        } else
            throw new IllegalArgumentException("In method " + this.getClass().getName() + "."
                    + this.getClass().getEnclosingMethod() + "() ,transaction must not be null");
    }

    /**
     * calculate the total withdraw amount of a transaction: amount +commission.
     * 
     * @param amount the amount to withdraw.
     * @return the total to withdraw.
     */
    public double calculateAmountCommission(double amount) {

        if (amount >= 0.01d && amount <= 2) {
            return 0.01d;
        } else if (amount > 2) {
            BigDecimal result = BigDecimal.valueOf(amount * ApplicationTransaction.COMMISSIONPERCENT).setScale(2,
                    RoundingMode.HALF_UP);
            return result.doubleValue();
        } else {
            throw new IllegalArgumentException("In method " + this.getClass().getName() + "."
                    + this.getClass().getEnclosingMethod() + "() ,amount must be positive!");
        }

    }

    /**
     * proceed and create and save a transaction between sender and receiver. the
     * application accounts of user will be credited/withdrawed with amount of saved
     * transaction.
     * Arguments of method are not null because verified by controller before.
     * 
     * @param transaction application transaction with amount to send , commission's
     *                    amount, description and date.Amount of transaction must pe
     *                    positive.
     * @param sender      user that send the amount of transaction and have to pay
     *                    commission.
     * @param receiver    the user that receive amount of transaction.
     * @return the transaction saved with all updated field.
     */
    @Transactional(rollbackFor = { RuntimeException.class, Exception.class })
    public ApplicationTransaction proceedBetweenUsers(ApplicationTransaction transaction, User sender, User receiver) {

        transaction.setTransactionDate(new Date());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmountCommission(this.calculateAmountCommission(transaction.getAmount()));
        transaction.setType(TransactionType.WIHTDRAW);

        appAccountService.withdraw(transaction.getSender().getApplicationAccount(),
                (transaction.getAmount() + transaction.getAmountCommission()));
        appAccountService.credit(transaction.getReceiver().getApplicationAccount(), transaction.getAmount());

        return appTransactionRepository.save(transaction);
    }

    /**
     * proceed and create and save a transaction between user and his bank. the
     * application accounts of user will be credited/withdrawed with amount of saved
     * transaction.
     * Arguments of method are not null because verified by controller before.
     * 
     * @param transaction application transaction with amount to send , commission's
     *                    amount, description and date.Amount of transaction must pe
     *                    positive.
     * @param bankAccountOwner      owner of bank account
     * @return the transaction saved with all updated field.
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public ApplicationTransaction proceedBankTransaction(ApplicationTransaction bankTransaction,User bankAccountOwner) {

        bankTransaction.setTransactionDate(new Date());
        bankTransaction.setSender(bankAccountOwner);
        bankTransaction.setReceiver(bankAccountOwner);

        switch (bankTransaction.getType()) {

            case WIHTDRAW:
                appAccountService.withdraw(bankTransaction.getSender().getApplicationAccount(),
                        (bankTransaction.getAmount() + bankTransaction.getAmountCommission()));
                bankAccountService.credit(bankTransaction.getSender().getBankAccount(), bankTransaction.getAmount());
                break;

            case CREDIT:
                bankAccountService.withdraw(bankTransaction.getSender().getBankAccount(),
                        (bankTransaction.getAmount() + bankTransaction.getAmountCommission()));
                appAccountService.credit(bankTransaction.getSender().getApplicationAccount(),
                        bankTransaction.getAmount());
                break;

            default:

        }
        return appTransactionRepository.save(bankTransaction);
    }

}
