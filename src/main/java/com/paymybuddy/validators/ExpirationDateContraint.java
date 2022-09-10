package com.paymybuddy.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = ExpirationDateValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpirationDateContraint {
    String message() default "Invalid expiration date: must be 'MM-YY' and after today";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
}
