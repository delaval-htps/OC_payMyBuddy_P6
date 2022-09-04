package com.paymybuddy.dto;

import javax.validation.constraints.NotBlank;

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
    
    @NotBlank(message = "the number of card must be filled in")
    private String cardNumber;
    
    @NotBlank(message = "the code of card must be filled in")
    private String cardCode;
    
    @NotBlank(message = "the ExpirationDate of card must be filled in")
    private String expirationDate;
}
