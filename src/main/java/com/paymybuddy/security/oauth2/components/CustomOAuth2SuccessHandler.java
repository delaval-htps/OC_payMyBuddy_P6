package com.paymybuddy.security.oauth2.components;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paymybuddy.security.oauth2.user.CustomOAuth2User;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();

        log.info("email of user is  {}", user.getEmail());
        log.info("customOauht2User: {}", user.toString());

        response.sendRedirect("/home");
    }

}
