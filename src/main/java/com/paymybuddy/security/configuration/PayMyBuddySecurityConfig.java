package com.paymybuddy.security.configuration;

import com.paymybuddy.security.components.OAuth2AuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class PayMyBuddySecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String HOME = "/home";

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private AuthenticationProvider oAuth2AuthenticationProvider;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth.authenticationProvider(oAuth2AuthenticationProvider);
    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests().antMatchers("/css/**").permitAll() // allowed to access to mycss.css
        .antMatchers(HOME).hasRole("USER")
        .antMatchers("/registration").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/loginPage")
        .loginProcessingUrl("/login")
        .permitAll()
        .and()
        .oauth2Login()
        .loginPage("/loginPage")
        .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"))
        .permitAll()
        .and()
        .logout()
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

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {

    return super.authenticationManagerBean();
  }

}
