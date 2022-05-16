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
   * retrieve a user by his id.
   * 
   * @param userId the user's id
   * @return a optional user if user existed in bdd.
   */
  public Optional<User> findById(Long userId) {
    return userRepository.findById(userId);
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

  /**
   * retrieve a list of connected users by given email of user
   * 
   * @param email email of User which we want to know his connected users
   * @return list of connected users of user's email
   */
  public List<User> findConnectedUserByEmail(String email) {
    return userRepository.findConnectedUserByEmail(email);
  }

  /**
   * save a user in application.
   *
   * @param user user to save
   * @return the user if he was saved
   */
  public User save(User user) {
    return userRepository.save(user);
  }
}
