package com.paymybuddy.security.oauth2.services;

import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import com.paymybuddy.security.oauth2.user.user_info.OAuth2UserInfoFactory;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User loadUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        return new CustomOAuth2User(loadUser,
                OAuth2UserInfoFactory.getOAuth2UserInfoService(registrationId, loadUser.getAttributes()));
    }

   
}
