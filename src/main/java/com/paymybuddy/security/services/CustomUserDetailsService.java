package com.paymybuddy.security.services;

import java.util.Optional;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Class to create your customized userDetails from the userNamePasswordAuthentication request we
 * can retrieve informations about the user that we need. If user he's already registred in
 * datasource he goes to home but if it is the first connection he will be redirected to
 * registration to fill in form and will be registred
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Put information of user in UserDetails to easely retrieve them.
     * 
     * @param username the email of connected user to retrieve him frm database.
     * @return UserDetails list of informations about user.
     * @throws UsernameNotFoundException if usr is not found in database.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // email is by definition the username of a user
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("the user with email:" + username + " was not found");
        } else {
            User existedUser = user.get();

            return new org.springframework.security.core.userdetails.User(existedUser.getEmail(), existedUser.getPassword(), existedUser.getRoles());
        }
    }

}
