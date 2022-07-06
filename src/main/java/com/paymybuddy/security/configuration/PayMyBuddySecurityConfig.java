package com.paymybuddy.security.configuration;

import com.paymybuddy.security.oauth2.components.CustomOAuth2SuccessHandler;
import com.paymybuddy.security.oauth2.services.CustomOAuth2UserService;
import com.paymybuddy.security.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
    http
      .authorizeRequests()
      .antMatchers("/css/**","/images/**")
      .permitAll()
      .antMatchers("/oauth2/**")
      .permitAll()
      .antMatchers("/login/**")
      .permitAll()
      .antMatchers("/logout")
      .permitAll()
      .antMatchers("/home", "/transfert/**","/profile/**","/contact")
      .hasRole("USER")
      .antMatchers("/registration")
      .permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .formLogin()
      .loginPage("/loginPage")
      .loginProcessingUrl("/login")
      .defaultSuccessUrl("/home",true)
      .permitAll()
      .and()
      .oauth2Login()
      .loginPage("/loginPage")
      .defaultSuccessUrl("/home")
      .userInfoEndpoint()
      .userService(customOAuth2UserService)
      .and()
      .successHandler(customOAuth2SuccessHandler)
      .and()
      .logout()
      .logoutSuccessUrl("/loginPage?logout")
      // to permit href:/logout without using form method post
      .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
      .invalidateHttpSession(true)
      .clearAuthentication(true)
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
}
