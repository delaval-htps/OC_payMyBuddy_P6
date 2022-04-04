package com.paymybuddy.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {

    private static final double COMMISIONPERCENT = 0.5d;

    public enum TransactionType {
        DEBIT, CREDIT;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Date transactionDate;

    private String description;

    private int amount;

    private TransactionType transactionType;

    private Long userId;

    private Long connectionUserId;

}
