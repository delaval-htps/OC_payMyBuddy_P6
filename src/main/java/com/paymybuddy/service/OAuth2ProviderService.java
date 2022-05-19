package com.paymybuddy.service;

import java.util.Optional;
import com.paymybuddy.exceptions.OAuth2ProviderNotFoundException;
import com.paymybuddy.model.AuthProvider;
import com.paymybuddy.model.OAuth2Provider;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.OAuth2ProviderRepository;
import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OAuth2ProviderService {

    @Autowired
    private OAuth2ProviderRepository oAuth2ProviderRepository;

    @Autowired
    private UserService userService;

    /**
     * Retrieve a Oauth2Provider of a user by his email and registrationClient of provider.
     * 
     * @param email email of user
     * @param provider name of Oauth2provider used by user to connect to appplication
     * @return Optional of Oauht2provider containing registration client (facebook, github...) and
     *         userId of user for this registration client.
     */
    public Optional<OAuth2Provider> getOAuht2ProviderByEmail(String email, AuthProvider provider) {
        return oAuth2ProviderRepository.findByEmail(email, provider);
    }

    /**
     * Save or update a OAuth2Provider of a existed user in bdd logged with OAuth2login.
     * 
     * @param oAuth2User the customOAuth2User logged
     * @param existedUser the existed user with the same email that OAuth2User
     * @return registred Oauth2provider if success or null if not.
     */

    public OAuth2Provider saveOAuth2ProviderForUser(CustomOAuth2User oAuth2User, User existedUser) {

        OAuth2Provider newOAuth2Provider = new OAuth2Provider();

        if (oAuth2User.getClientId() != null && oAuth2User.getClientRegistrationId() != null) {
            newOAuth2Provider.setProviderUserId(oAuth2User.getClientId());

            AuthProvider enumProvider = AuthProvider.fromString(oAuth2User.getClientRegistrationId());
            if (enumProvider != null) {
                newOAuth2Provider.setRegistrationClient(enumProvider);
            } else {
                throw new OAuth2ProviderNotFoundException("This Oauht2Provider " + oAuth2User.getClientRegistrationId() + " is not approuved by paymybuddy.");
            }

            existedUser.addOAuth2Identifier(newOAuth2Provider);
            userService.save(existedUser);
        } else {
            throw new OAuth2ProviderNotFoundException("problem occurs: Oauth2provider of CustomOAuth2User logged doesn't have registrationId and clientId!");
        }

        return newOAuth2Provider;
    }
}
