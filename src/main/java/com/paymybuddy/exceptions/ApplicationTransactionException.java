package com.paymybuddy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * custom exception for error with any transaction in application
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ApplicationTransactionException extends RuntimeException {
    public ApplicationTransactionException(String message) {
        super(message);
    }
}
