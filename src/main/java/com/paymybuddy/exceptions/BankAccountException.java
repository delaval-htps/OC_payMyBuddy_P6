package com.paymybuddy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * custom exception for error with bankAccount in application
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class BankAccountException  extends RuntimeException {
    public BankAccountException(String message) {
        super(message);
    }
}

