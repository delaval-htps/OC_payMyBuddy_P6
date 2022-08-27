package com.paymybuddy.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * the Dto of a bank account of user .
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountDto {


    @NotBlank(message = "the iban must not be null or empty")
    @Length(min = 38, max = 38, message = "the iban must contains 31 characters")
    // @Pattern(regexp =
    // "\b[A-Z]{4}-[A-Z]{2}[0-9]{2}-[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{3}\b",message
    // = "the iban must contain 6 charaters first and 25 integers")
    private String iban;

    @NotBlank(message = "the bic must not be null or empty")
    @Size(min = 8, max = 8, message = "the bic must only contain 8 characters")
    @Pattern(regexp = "[A-Z]{8}", message = "the bic must contain 8 characters")
    private String bic;


}
