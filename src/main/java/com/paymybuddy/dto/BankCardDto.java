package com.paymybuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
/**
 * BankCard can be fill in or not .No need to have a bankcard for user.
 */
public class BankCardDto {
    private String cardNumber;
    private String cardCode;
    private String expirationDate;
}
