package com.paymybuddy.dto;

import java.math.BigDecimal;
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

    @Digits(integer = 8, fraction = 2, message = "this amount must have 8 integers before coma and only 2 after")
    @Positive
    private BigDecimal amount;

    @NotEmpty
    private String description;

    @NotEmpty
    private String type;

    @NotEmpty
    @Email
    private String senderEmail;

    @NotEmpty
    @Email
    private String receiverEmail;

}
