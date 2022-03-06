package com.paymybuddy;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class UtilService {

  public static String getRandomIban() throws NoSuchAlgorithmException {

    Random rand = SecureRandom.getInstanceStrong();
    StringBuilder numberAccounString = new StringBuilder();

    numberAccounString.append("IBAN FR");
    numberAccounString.append(String.format("%02d", rand.nextInt(99)));
    numberAccounString.append(" ");

    for (int i = 0; i < 5; i++) {
      numberAccounString.append(String.format("%04d", rand.nextInt(9999)));
      numberAccounString.append(" ");
    }

    numberAccounString.append(String.format("%03d", rand.nextInt(999)));

    return numberAccounString.toString();

  }

  public static String getRandomAccountNumber() throws NoSuchAlgorithmException {

    Random rand = SecureRandom.getInstanceStrong();
    StringBuilder numberAccounString = new StringBuilder();

    // for number account
    numberAccounString.append(String.format("%06d", rand.nextInt(999999)));
    numberAccounString.append(String.format("%05d", rand.nextInt(99999)));
    // for key RIB
    numberAccounString.append(" ");
    numberAccounString.append(String.format("%02d", rand.nextInt(99)));

    return numberAccounString.toString();

  }
}
