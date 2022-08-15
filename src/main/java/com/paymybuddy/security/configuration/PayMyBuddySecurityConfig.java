package com.paymybuddy.security.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class PayMyBuddySecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private CustomOAuth2UserService customOAuth2UserService;

  @Autowired
  private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(customUserDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/css/**", "/images/**")
        .permitAll()
        .antMatchers("/oauth2/**")
        .permitAll()
        .antMatchers("/login/**")
        .permitAll()
        .antMatchers("/logout")
        .permitAll()
        .antMatchers("/", "/home", "/transfert/**", "/profile/**", "/contact")
        .hasRole("USER")
        .antMatchers("/registration")
        .permitAll()
        .anyRequest()
        .authenticated();

    http.formLogin()
        .loginPage("/loginPage")
        .loginProcessingUrl("/login")
        .defaultSuccessUrl("/home", true)
        .permitAll();

    http.oauth2Login()
        .loginPage("/loginPage")
        .defaultSuccessUrl("/home")
        .userInfoEndpoint()
        .userService(customOAuth2UserService)
        .and()
        .successHandler(customOAuth2SuccessHandler)
        .permitAll();

    http.logout()
        // to permit href:/logout without using form method post
        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        // .addLogoutHandler(new ProperCookieClearingLogoutHandler("JSESSIONID"))
        .deleteCookies("JSESSIONID")
        .clearAuthentication(true)
        .invalidateHttpSession(true)
        .logoutSuccessUrl("/loginPage?logout")
        .permitAll();

    http.rememberMe()
        .key("uniqueAndSecret")
        .rememberMeParameter("remember-me")
        .tokenValiditySeconds(600);

    http.sessionManagement()
        .maximumSessions(1)
        .expiredUrl("/loginPage?logout")
        .sessionRegistry(sessionRegistry());

  }

  @Bean
  SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  @Bean
  public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
    return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
