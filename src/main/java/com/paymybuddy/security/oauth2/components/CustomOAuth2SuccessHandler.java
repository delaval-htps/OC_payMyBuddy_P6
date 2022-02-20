package com.paymybuddy.security.oauth2.components;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import com.paymybuddy.model.OAuth2Provider;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.security.oauth2.user.CustomOAuth2User;
import com.paymybuddy.service.OAuth2ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

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

      Optional<OAuth2Provider> oAuth2ProviderUser =
          oAuth2ProviderService.getOAuht2ProviderByEmail(currentUser.getEmail());

      if (!oAuth2ProviderUser.isPresent()) {
        // case of OAuth2Provider is not registred for this user
        oAuth2ProviderService.saveOAuth2ProviderForUser(user, currentUser);
      }
        // change Oauth2authenticationToken in UsernamePasswordAuthenticationToken
      SecurityContext context = SecurityContextHolder.getContext();
      SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(simpleGrantedAuthority);
      UsernamePasswordAuthenticationToken newUser = new UsernamePasswordAuthenticationToken(
          currentUser, currentUser.getPassword(), authorities);
      context.setAuthentication(newUser);

      response.sendRedirect("/home");

    } else {
      // case of user not registred
      response.sendRedirect("/registration");
    }
  }


}
