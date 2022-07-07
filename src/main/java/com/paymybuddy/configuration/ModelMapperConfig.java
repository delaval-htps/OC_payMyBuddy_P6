package com.paymybuddy.configuration;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.paymybuddy.dto.ApplicationTransactionDto;
import com.paymybuddy.dto.BankCardDto;
import com.paymybuddy.model.ApplicationTransaction;
import com.paymybuddy.model.BankCard;
import com.paymybuddy.model.ApplicationTransaction.TransactionType;

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
  public Converter<String, Date> convertStringToDate() {
    return this::extracted;
  }

  private Date extracted(MappingContext<String, Date> context) {
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    Date date = new Date();
    try {
      date = df.parse(context.getSource());
    } catch (java.text.ParseException e) {
      e.printStackTrace();
    }
    return date;
  }

  @Bean
  public Converter<BigDecimal, Double> convertBigDecimalToDouble() {
    return context -> (context.getSource()).doubleValue();
  }

  @Bean
  public Converter<String, TransactionType> convertStringToEnum() {
    return context -> context.getSource().equalsIgnoreCase("CREDIT") ? TransactionType.CREDIT : TransactionType.WIHTDRAW;
  }

  PropertyMap<BankCardDto, BankCard> bankCardMap = new PropertyMap<BankCardDto, BankCard>() {

    @Override
    protected void configure() {
      using(convertStringToInteger()).map(source.getCardCode(), destination.getCardCode());
      using(convertStringToDate()).map(source.getExpirationDate(), destination.getExpirationDate());
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
