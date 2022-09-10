package com.paymybuddy.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.paymybuddy.validators.ExpirationDateContraint;

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
    @Pattern(regexp="^((\\d{4}\\s){3}(\\d{4}))$",message = "the number must contains 16 digits")
    private String cardNumber;
    
    @NotBlank(message = "the code of card must be filled in")
    @Pattern(regexp="[\\d]{3}",message="the code must contains 3 digits")
    private String cardCode;
    
    @NotBlank(message = "the ExpirationDate of card must be filled in")
    @ExpirationDateContraint
    private String expirationDate;
}
