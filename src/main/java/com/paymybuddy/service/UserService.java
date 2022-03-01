package com.paymybuddy.service;

import java.util.List;
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
   * save a user in application.
   *
   * @param user user to save
   * @return the user if he was saved
   */
  public User save(User user) {

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

public List<User> findConnectedUserByEmail(String email) {
    return userRepository.findConnectedUserByEmail(email);
}

}
