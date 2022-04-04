package com.paymybuddy.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Date transactionDate;

    private String description;

    private double commisionPercent;

    private int amount;

    private String userEmail;

    private Long connectionUserEmail;

}
