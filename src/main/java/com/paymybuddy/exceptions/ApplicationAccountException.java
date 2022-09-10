package com.paymybuddy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * custom exception for error with application's account in application
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class ApplicationAccountException extends RuntimeException {
    
    public ApplicationAccountException(String message){
        super(message);
    }
}
