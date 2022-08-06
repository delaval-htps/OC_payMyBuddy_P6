package com.paymybuddy.security.oauth2.services;

import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import com.paymybuddy.security.oauth2.user.user_info.OAuth2UserInfoFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * Class to create your customized OAuth2User from the Oauth2 request from registrationId client. We
 * create a CustomOAuth2UserService to customize DefaultOauth2User and accept only Oauht2Login from
 * some registrations id like FACEBOOK,GITHUB,GOOGLE. Because of using
 * OAuth2UserInfoFactory.getOAuth2UserInfoService(registrationId, loadUser.getAttributes())), we can
 * retrieve informations about the user that we need. If user he's already registred in datasource
 * he goes to home but if it is the first connection he will be redirected to registration and his
 * lastname firstname and email were be fill in form.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    /**
     * We load User from request and create your customOAuth2User that implements Oauth2User.
     * 
     * @param OAuth2UserRequest the request from registrationId client with informations of user.
     * @return OAuth2User the OAuth2User that authenticated but customized with informations that we
     *         need.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // we retrieve th default Oauth2User from the Oauth2request and after transfom it in your
        // customOauth2user
        OAuth2User loadUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // after retrieve his registrationId we can verifie correctly his informations to after display them
        // inform of registration
        return new CustomOAuth2User(loadUser, OAuth2UserInfoFactory.getOAuth2UserInfoService(registrationId, loadUser.getAttributes()));
    }


}
