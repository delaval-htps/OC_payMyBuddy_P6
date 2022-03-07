package com.paymybuddy;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * singleton class (cause of springboot) to create unique random account number or iban. Use of set
 * to save account numbers and verify if new account number generated is contained in it (with the
 * size of set) to be sure it's unique.
 * 
 * @return void
 * @throws NoSuchAlgorithmException
 */
@Component
@NoArgsConstructor
@Getter
@Setter
public class UtilService {

  private static Set<StringBuilder> accountNumbers = new HashSet<>();

  public String getRandomApplicationAccountNumber() throws NoSuchAlgorithmException {

    int lengthOfAccountNumbers = accountNumbers.size();
    Random rand = SecureRandom.getInstanceStrong();
    StringBuilder accountNumberString = new StringBuilder();

    // as new generated account number is added to set , if set.size() > initial size then new
    // account number is unique
    while (accountNumbers.size() == lengthOfAccountNumbers) {

      // for number account
      accountNumberString.append(String.format("%06d", rand.nextInt(999999)));
      accountNumberString.append(String.format("%05d", rand.nextInt(99999)));

      // for key RIB
      accountNumberString.append(" ");
      accountNumberString.append(String.format("%02d", rand.nextInt(99)));
      accountNumbers.add(accountNumberString);
    }
    return accountNumberString.toString();

  }
}
