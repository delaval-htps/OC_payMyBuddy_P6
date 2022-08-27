package com.paymybuddy.dto;

import java.math.BigDecimal;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * the Dto of a transaction .
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationTransactionDto {

    @Digits(integer = 8, fraction = 2, message = "this amount must have 8 integers before coma and only 2 after")
    @Positive(message="the amount must be positive number")
    @NotNull(message="the amount of transaction must be not null")
    private BigDecimal amount;

    @NotBlank(message="the description of transaction must be not null")
    private String description;

    @NotBlank(message="the type of transaction must be not null")
    private String type;

    @NotBlank(message="the connected user of transaction must be not empty")
    @Email
    private String senderEmail;

    @NotBlank(message="the connected user of transaction must be not empty")
    @Email
    private String receiverEmail;

}
