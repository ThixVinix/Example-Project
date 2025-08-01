package com.example.exampleproject.configs.annotations.repeatables;

import com.example.exampleproject.configs.annotations.DateRangeValidation;
import com.example.exampleproject.configs.annotations.validators.DateRangeValidator;
import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateRangeValidator.class)
public @interface ValidDateRanges {
    DateRangeValidation[] value();
}