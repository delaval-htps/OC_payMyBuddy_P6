package com.paymybuddy.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class that represents any transaction in application . it can be a withdraw
 * or credit on bank Account or between users .
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "transaction")
public class ApplicationTransaction implements Serializable {

    public enum TransactionType {
        WITHDRAW, CREDIT
    }

    public static final double COMMISSIONPERCENT = 0.05d;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date transactionDate;

    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private double amount;

    private double amountCommission;

    @ManyToOne(cascade = { CascadeType.MERGE,  CascadeType.REFRESH })
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @OneToOne(cascade= CascadeType.ALL,mappedBy="transaction")
    private Invoice invoice;
}
