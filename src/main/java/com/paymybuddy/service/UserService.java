package com.paymybuddy.service;

import java.util.Optional;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  /**
   * return the user authenticated by Oauth2 with his username and email if he's
   * registred in
   * application.
   *
   * @param username the username of user authenticated by Oauth2
   * @param email    the email of this user
   * @return return user if he's registred in application, null if not.
   */
  // public Optional<User> findUserByOauth2Information(String username, String
  // email) {
  // String lastname = null;
  // String firstname = null;

  // if (username != null && !username.trim().equalsIgnoreCase("")) {
  // firstname = username.trim().split(" ")[0];
  // lastname = username.trim().split(" ")[1];
  // }
  // return userRepository.findbyOauth2Information(firstname, lastname, email);
  // }

  /**
   * save a user in application.
   *
   * @param user user to save
   * @return the user if he was saved
   */
  public User saveUser(User user) {

    return userRepository.save(user);
  }

  /**
   * retrieve a user with his email.
   * 
   * @param email the email of user
   * @return return the user if existing or null if not
   */
  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }
}
