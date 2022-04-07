package com.paymybuddy.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationTransactionDto {

    private int amount;

    @NotEmpty
    private String description;

    @NotEmpty
    @Email
    private String userEmail;

    @NotEmpty
    @Email
    private String connectionUserEmail;

}
