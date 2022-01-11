package com.paymybuddy.security.configuration;

import com.paymybuddy.security.components.OAuth2AuthenticationProvider;
import com.paymybuddy.security.components.OAuth2SuccessHandler;
import com.paymybuddy.security.services.CustomOAuth2UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class PayMyBuddySecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String HOME = "/home";

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private CustomOAuth2UserService customOAuth2UserService;

  @Autowired
  private OAuth2SuccessHandler oAuth2SuccessHandler;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests()
        .antMatchers("/css/**").permitAll()
        .antMatchers(HOME).hasRole("USER")
        .antMatchers("/registration").permitAll()
        .anyRequest().authenticated();

    http.formLogin()
        .loginPage("/loginPage")
        .loginProcessingUrl("/login")
        .permitAll();

    http.oauth2Login()
        .loginPage("/loginPage")
        .userInfoEndpoint()
        .userService(customOAuth2UserService).and()
        .successHandler(successHandler)
        .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"))
        .permitAll();

    http.logout()
        .logoutSuccessUrl("/loginPage?logout")
        .deleteCookies("JSESSIONID")
        .permitAll()
        .and()
        .rememberMe()
        .key("uniqueAndSecret");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {

    return super.authenticationManagerBean();
  }

}
