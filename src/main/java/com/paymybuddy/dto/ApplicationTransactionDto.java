package com.paymybuddy.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationTransactionDto {

    @Digits(integer = 8, fraction = 2)
    @Positive
    private double amount;

    @NotEmpty
    private String description;

    @NotEmpty
    @Email
    private String senderEmail;

    @NotEmpty
    @Email
    private String receiverEmail;

}
