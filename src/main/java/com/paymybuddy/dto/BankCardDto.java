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
public class BankCardDto {
    @NotBlank
    private String cardNumber;
    @NotBlank
    private String cardCode;
    private String expirationDate;
}
