package com.paymybuddy.configuration;

import java.math.BigDecimal;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.dto.BankCardDto;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.ApplicationTransaction.TransactionType;

/**
 * Class of configuration for ModelMapper to convert correctly Dto in entity.
 */
@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper mm = new ModelMapper();
    mm.addMappings(bankCardMap);
    mm.addMappings(appTransactionMap);
    return mm;
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
  public Converter<BigDecimal, Double> convertBigDecimalToDouble() {
    return context -> (context.getSource()).doubleValue();
  }

  @Bean
  public Converter<String, TransactionType> convertStringToEnum() {
    return context -> context.getSource().equalsIgnoreCase("CREDIT") ? TransactionType.CREDIT
        : TransactionType.WITHDRAW;
  }

  PropertyMap<BankCardDto, BankCard> bankCardMap = new PropertyMap<BankCardDto, BankCard>() {

    @Override
    protected void configure() {
      using(convertStringToInteger()).map(source.getCardCode(), destination.getCardCode());
     
    }
  };

  PropertyMap<ApplicationTransactionDto, ApplicationTransaction> appTransactionMap = new PropertyMap<ApplicationTransactionDto, ApplicationTransaction>() {

    @Override
    protected void configure() {
      using(convertBigDecimalToDouble()).map(source.getAmount(), destination.getAmount());
      using(convertStringToEnum()).map(source.getType(), destination.getType());
    }
  };
}
