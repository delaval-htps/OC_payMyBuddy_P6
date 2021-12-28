package com.paymybuddy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public boolean login(String username, String password) {

        // we find the user with userDeatailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // need usernamePasswordAuthenticationToken to pass it to authenticationManager
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, password,
                userDetails.getAuthorities());

        // pass the token to authenticationManager
        authenticationManager.authenticate(token);

        // check the token if it is authenticated
        boolean result = token.isAuthenticated();

        // if authenticated we have to put it in securityContextHolder !
        if (result) {
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        return result;
    }

}
