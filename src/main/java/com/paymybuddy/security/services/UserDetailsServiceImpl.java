package com.paymybuddy.security.services;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // email is by definition the username of a user
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("the user with email:" + username + " was not found");
        } else {

            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                    user.getRoles());
        }
    }
}