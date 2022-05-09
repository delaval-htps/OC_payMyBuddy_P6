package com.paymybuddy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
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
    return new Converter<String, Integer>() {
      public Integer convert(MappingContext<String, Integer> context) {
        return Integer.parseInt(context.getSource());
      }
    };
  }

  @Bean
  public Converter<String, Long> convertStringToLong() {
    return new Converter<String, Long>() {
      public Long convert(MappingContext<String, Long> context) {
        return Long.parseLong(context.getSource());
      }
    };
  }

  @Bean
  public Converter<String, Date> convertStringToDate() {
    return new Converter<String, Date>() {
      public Date convert(MappingContext<String, Date> context) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        try {
          date = df.parse(context.getSource());
        } catch (ParseException e) {
          e.printStackTrace();
        }
        return date;
      }
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(PayMyBuddyApplication.class, args);
  }

}
