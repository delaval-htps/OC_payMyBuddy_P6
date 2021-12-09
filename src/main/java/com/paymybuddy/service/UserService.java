package com.paymybuddy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

  @Autowired private UserRepository userRepository;

  @Autowired private AuthoritiesService authoritiesService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username);

    if (user == null) {
      throw new UsernameNotFoundException("No user found with username:" + username);
    }

    List<String> userRoles = authoritiesService.getRoles(user.getEmail());

    return new org.springframework.security.core.userdetails.User(
        user.getEmail(), user.getPassword().toLowerCase(), getAuthorities(userRoles));
  }

  private List<GrantedAuthority> getAuthorities(List<String> userRoles) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    for (String role : userRoles) {
      authorities.add(new SimpleGrantedAuthority(role));
    }
    return authorities;
  }
  /**
   * return the user authenticated by Oauth2 with his username and email if he's registred in
   * application.
   *
   * @param username the username of user authenticated by Oauth2
   * @param email the email of this user
   * @return return user if he's registred in application, null if not.
   */
  public Optional<User> findUserByOauth2Information(String username, String email) {
    String lastname = null;
    String firstname = null;

    if (username != null && !username.trim().equalsIgnoreCase("")) {
      firstname = username.trim().split(" ")[0];
      lastname = username.trim().split(" ")[1];
    }
    return userRepository.findbyOauth2Information(firstname, lastname, email);
  }
  /**
   * save a user in application.
   *
   * @param user user to save
   * @return the user if he was saved
   */
  public User saveUser(User user) {

    return userRepository.save(user);
  }

  public User findByEmail(String email) {
    return userRepository.findByEmail(email);
  }
}
