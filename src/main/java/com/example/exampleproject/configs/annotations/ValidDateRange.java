package com.example.exampleproject.configs.annotations;

import com.example.exampleproject.configs.annotations.repeatables.ValidDateRanges;
import com.example.exampleproject.configs.annotations.validators.DateRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = DateRangeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ValidDateRanges.class)
public @interface ValidDateRange {

    String message() default "Datas inválidas ou fora de ordem: {dateAField} e {dateBField}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    /**
     * Specifies the name of the first date field to be validated in a date range constraint.
     *
     * @return the name of the field representing the first date
     */
    String dateAField();


    /**
     * Specifies the name of the second date field to be validated in a date range constraint.
     *
     * @return the name of the field representing the second date
     */
    String dateBField();
}
