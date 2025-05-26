package com.example.exampleproject.configs.annotations.repeatables;

import com.example.exampleproject.configs.annotations.DateRangeValidation;
import com.example.exampleproject.configs.annotations.validators.DateRangeValidator;
import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRanges {
    DateRangeValidation[] value();
}