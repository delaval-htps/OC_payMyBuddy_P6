package com.paymybuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main class for application.
 */
@SpringBootApplication
public class PayMyBuddyApplication extends SpringBootServletInitializer {

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(PayMyBuddyApplication.class);
  }
  
  public static void main(String[] args) {
    SpringApplication.run(PayMyBuddyApplication.class, args);
  }


  
}
