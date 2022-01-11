package com.paymybuddy.security.components;

import java.util.Map;

import com.paymybuddy.security.services.GithubUserInfoServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private GithubUserInfoServiceImpl oAuth2Service;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Map<String, Object> oAuth2LoginInfo = oAuth2Service.getOauth2LoginInfo(authentication);

        String username = oAuth2LoginInfo.get("email").toString();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = null;

        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException exception) {
            throw new BadCredentialsException("user not found in database");
        }

        if (username.equals(userDetails.getUsername()) && password.equals(userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(),
                    userDetails.getAuthorities());
        } else {
            return null;
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {

        return authentication.equals(OAuth2AuthenticationToken.class);
    }

}
