package com.paymybuddy.security.oauth2.components;

import com.paymybuddy.exceptions.OAuth2ProviderNotFoundException;
import com.paymybuddy.model.AuthProvider;
import com.paymybuddy.model.OAuth2Provider;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import com.paymybuddy.service.OAuth2ProviderService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class CustomOAuth2SuccessHandler
    extends SimpleUrlAuthenticationSuccessHandler {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private OAuth2ProviderService oAuth2ProviderService;

  @Override
  @Transactional
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();

    Optional<User> existedUser = userRepository.findByEmail(user.getEmail());

    // case when user is already registred
    if (existedUser.isPresent()) {
      User currentUser = existedUser.get();

      Optional<OAuth2Provider> oAuth2ProviderUser = oAuth2ProviderService.getOAuht2ProviderByEmail(
          currentUser.getEmail());

      if (oAuth2ProviderUser.isPresent()) {
        // case of OAuth2Provider already registred in bdd
        response.sendRedirect("/home");

      } else {
        // case of OAuth2Provider is not registred for this user
        OAuth2Provider newOAuth2Provider = new OAuth2Provider();
        newOAuth2Provider.setProviderUserId(user.getClientId());

        AuthProvider enumProvider = AuthProvider.fromString(user.getClientRegistrationId());

        if (enumProvider != null) {
          newOAuth2Provider.setRegistrationId(enumProvider);
        } else {
          throw new OAuth2ProviderNotFoundException(
              "the Oauht2Provider is not approuved by paymybuddy.");
        }

        currentUser.addOAuth2Identifier(newOAuth2Provider);
        userRepository.save(currentUser);
      }
      response.sendRedirect("/home");

    } else {
      // case of user not registred

      response.sendRedirect("/registration");
    }
  }
}
