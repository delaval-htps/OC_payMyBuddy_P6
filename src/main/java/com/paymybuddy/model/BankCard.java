package com.paymybuddy.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class that represents a bnak card's of connected user.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankCard implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String cardNumber;

    @Column
    private int cardCode;

    @Column
    private Date expirationDate;

    @ManyToOne(cascade = { CascadeType.REFRESH, CascadeType.MERGE })
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @OneToOne(mappedBy = "bankCard")
    private User user;
}
