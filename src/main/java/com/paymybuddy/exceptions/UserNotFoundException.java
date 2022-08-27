package com.paymybuddy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * custom exception for error with any connected user not found in application
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

  /** */
  private static final long serialVersionUID = 1L;

  public UserNotFoundException(String message) {
    super(message);
  }
}
