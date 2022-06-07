package com.paymybuddy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PayMyBuddyApplication {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public Converter<String, Integer> convertStringToInteger() {
    return context -> Integer.parseInt(context.getSource());
  }

  @Bean
  public Converter<String, Long> convertStringToLong() {
    return context -> Long.parseLong(context.getSource());
  }

  @Bean
  public Converter<String, Date> convertStringToDate() {
    return context -> {
      SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
      Date date = new Date();
      try {
        date = df.parse(context.getSource());
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return date;
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(PayMyBuddyApplication.class, args);
  }

}
