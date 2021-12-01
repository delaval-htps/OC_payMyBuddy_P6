package com.paymybuddy.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public Optional<User> findByUserOauth2Information(String firstName, String lastName,
      String email) {

    return userRepository.findbyOauth2Information(firstName, lastName, email);
  }

  // public void saveNewUser(Map<String, Object> oauth2Information) {
  // User newUser = new User();
  // return userRepository.save(null)
  // }

}
