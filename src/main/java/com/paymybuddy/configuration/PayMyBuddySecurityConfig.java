package com.paymybuddy.configuration;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class PayMyBuddySecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private DataSource datasource;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    auth.jdbcAuthentication()
        .dataSource(datasource)
        .usersByUsernameQuery(
            "SELECT email,password,enabled FROM application_identifier WHERE email =?")
        .authoritiesByUsernameQuery(
            "SELECT email,'ROLE_USER' FROM application_identifier WHERE email=?")
        .passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.authorizeRequests()
        .antMatchers("/css/**").permitAll() // allowed to access to mycss.css
        .antMatchers("/home").hasRole("USER")
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/myLoginPage")
        .loginProcessingUrl("/authenticateTheUser")
        .permitAll()
        .and()
        .logout().permitAll();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
