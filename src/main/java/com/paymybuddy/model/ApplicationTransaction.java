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
@Entity(name = "transaction")
public class ApplicationTransaction {

    public static final double COMMISSIONPERCENT = 0.5d;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    private Date transactionDate;

    private String description;

    private int amount;

    private double amountCommission;

    private Long userId;

    private Long connectionUserId;

}
