package com.paymybuddy.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class BankCardDto {
    private int cardNumber;
    private int cardCode;
    private Date expirationDate;
}
