package com.paymybuddy.service;

import com.paymybuddy.UtilService;
import com.paymybuddy.exceptions.ApplicationAccountException;
import com.paymybuddy.exceptions.UserException;
import com.paymybuddy.model.ApplicationAccount;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ApplicationAccountRepository;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationAccountService {

  @Autowired
  private ApplicationAccountRepository applicationAccountRepository;

  @Autowired
  private UtilService utilService;


  /**
   * retrieve application account of a user by its id.
   * 
   * @param id id of application account
   * @return application account if it exists
   */
  public Optional<ApplicationAccount> findById(Long id) {
    return applicationAccountRepository.findById(id);
  }

  /**
   * retrieve user's application account by his email
   * 
   * @param email email of owner of applicationAccount
   * @return application account if it exists
   */
  public Optional<ApplicationAccount> findByEmail(String email) {
    return applicationAccountRepository.findByEmail(email);
  }

  /**
   * create a new application account of a user registred in db.
   * 
   * @param user the new user
   * @return application account initialized.
   * @throws NoSuchAlgorithmException
   */
  public ApplicationAccount createAccountforUser(User user) throws NoSuchAlgorithmException {

    if (user != null) {
      if (user.getApplicationAccount() == null) {
        ApplicationAccount appAccountOfUser = new ApplicationAccount();
        appAccountOfUser.setAccountNumber(utilService.getRandomApplicationAccountNumber());
        appAccountOfUser.setBalance(0d);
        user.setApplicationAccount(appAccountOfUser);
        return appAccountOfUser;
      } else {
        throw new ApplicationAccountException(
            "this user " + user.getFullName() + "has already an application account.");

      }
    } else {
      throw new UserException("For creation of application account,the user doesn't exist");
    }
  }
}
