package com.paymybuddy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * custom exception for error with a connection with a OAuth2provider not allowed in application.
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class OAuth2ProviderNotFoundException extends RuntimeException {

    public OAuth2ProviderNotFoundException(String message) {
        super(message);
    }
}
