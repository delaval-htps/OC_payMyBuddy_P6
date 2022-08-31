package com.paymybuddy;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * singleton class (cause of springboot) to create unique random account number or iban. Use of set
 * to save account numbers and verify if new account number generated is contained in it (with the
 * size of set) to be sure it's unique.
 */
@Component
@NoArgsConstructor
@Getter
@Setter
public class UtilService {


  public String getRandomApplicationAccountNumber()  {
    
    UUID uuid = UUID.randomUUID();
    return uuid.toString();
  }
}
