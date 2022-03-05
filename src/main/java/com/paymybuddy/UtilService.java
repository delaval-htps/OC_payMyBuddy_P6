package com.paymybuddy;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class UtilService {



  public static String getRandomAccountNumber() throws NoSuchAlgorithmException {
    Random rand = SecureRandom.getInstanceStrong();

    String startNumber = "FR";

    StringBuilder numberAccounString = new StringBuilder();

    numberAccounString.append(startNumber);
    numberAccounString.append(String.format("%02d", rand.nextInt(99)));
    for (int i = 0; i < 3; i++) {
      numberAccounString.append(" ");
      numberAccounString.append(String.format("%04d", rand.nextInt(99)));
    }

    return numberAccounString.toString();

  }
}
