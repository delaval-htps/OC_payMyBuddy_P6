package com.paymybuddy.validators;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExpirationDateValidator implements ConstraintValidator<ExpirationDateContraint, String> {

    @Override
    public void initialize(ExpirationDateContraint expirationDate) {
    }
    
    /**
     * allow to validate a expiration date for a bankCard.
     * The value must be not null and not empty because of validation ajax @NotBlank on field expirationDate in {@link com.paymybuddy.dto.BankCardDto}
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value!= null && !value.equalsIgnoreCase("")) {
            boolean firstValid = value.matches("^((0?[1-9]|1[012])-([0-9]{2}))$");

            boolean secondValid = false;
            SimpleDateFormat df = new SimpleDateFormat("MM-yy");
            try {
                secondValid = df.parse(value).after(new Date())||df.parse(value).equals(new Date());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return firstValid && secondValid;
        }
        return true;
    }

}
