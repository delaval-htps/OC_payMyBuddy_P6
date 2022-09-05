package com.paymybuddy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * the Dto for a Account: either a application'zs account or bank's account.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationAccountDto {

    private double balance;

}
