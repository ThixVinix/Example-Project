package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.ValidDateRange;
import com.example.exampleproject.utils.DateUtils;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.*;

@Slf4j
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String dateAField;
    private String dateBField;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.dateAField = constraintAnnotation.dateAField();
        this.dateBField = constraintAnnotation.dateBField();
    }

    /**
     * Validates if the provided object's date fields follow a specific relation.
     *
     * @param value   the object containing the date fields to be validated
     * @param context context in which the constraint is evaluated
     * @return true if the date in dateAField is not after the date in dateBField, otherwise false
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        boolean isValid = true;

        try {
            Object dateAValue = value.getClass().getMethod(dateAField).invoke(value);
            Object dateBValue = value.getClass().getMethod(dateBField).invoke(value);

            if (dateAValue == null && dateBValue == null) {
                return true;
            }

            if (dateAValue == null) {
                addConstraintViolationDateA(context,
                        MessageUtils.getMessage(
                                "msg.validation.request.field.date.range.empty", dateAField, dateBField));
                isValid = false;
            } else if (dateBValue == null) {
                addConstraintViolationDateB(context,
                        MessageUtils.getMessage(
                                "msg.validation.request.field.date.range.empty", dateAField, dateBField));
                isValid = false;
            } else {
                Instant instantA = DateUtils.toInstant(dateAValue);
                Instant instantB = DateUtils.toInstant(dateBValue);

                if (instantA.isAfter(instantB)) {
                    addConstraintViolationDateA(context,
                            MessageUtils.getMessage(
                                    "msg.validation.request.field.date.range.invalid", dateAField, dateBField));
                    isValid = false;
                }
            }

        } catch (Exception e) {
            log.warn("Error validating date range: {}", e.getMessage(), e);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Adds a constraint violation message to the validation context.
     *
     * @param context the {@link ConstraintValidatorContext} into which the violation should be added
     */
    private void addConstraintViolationDateA(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(dateAField)
                .addConstraintViolation();
    }

    private void addConstraintViolationDateB(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(dateBField)
                .addConstraintViolation();
    }

}