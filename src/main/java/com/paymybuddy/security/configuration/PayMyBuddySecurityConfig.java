package com.paymybuddy.security.configuration;

import com.paymybuddy.security.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
public class PayMyBuddySecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private UserDetailsServiceImpl userDetailsService;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests().antMatchers("/css/**").permitAll() // allowed to access to mycss.css
        .antMatchers("/home").hasRole("USER")
        .antMatchers("/registration").permitAll()
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/myLoginPage")
        .loginProcessingUrl("/authenticateTheUser")
        .permitAll()
        .and()
        .oauth2Login()
        .loginPage("/myLoginPage")
        .defaultSuccessUrl("/")
        .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"))
        .and()
        .logout()
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
