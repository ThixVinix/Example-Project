package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.ValidDateRange;
import com.example.exampleproject.utils.messages.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.util.Date;

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

        try {
            Object dateAValue = value.getClass().getMethod(dateAField).invoke(value);
            Object dateBValue = value.getClass().getMethod(dateBField).invoke(value);

            if (dateAValue == null || dateBValue == null) {
                addConstraintViolation(context,
                        MessageUtils.getMessage(
                                "msg.validation.request.field.date.range.empty", dateAField, dateBField));
                return false;
            }

            Instant instantA = toInstant(dateAValue);
            Instant instantB = toInstant(dateBValue);

            if (instantA.isAfter(instantB)) {
                addConstraintViolation(context,
                        MessageUtils.getMessage(
                                "msg.validation.request.field.date.range.invalid", dateAField, dateBField));
                return false;
            }

            return true;
        } catch (Exception e) {
            log.warn("Error validating date range: {}", e.getMessage(), e);
            addConstraintViolation(context, e.getMessage());
            return false;
        }
    }

    /**
     * Adds a constraint violation message to the validation context.
     *
     * @param context the {@link ConstraintValidatorContext} into which the violation should be added
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(dateAField)
                .addConstraintViolation();
    }


    /**
     * Converts various date/time types to Instant.
     *
     * @param dateObject the date/time object to convert
     * @return the Instant representation of the date/time object
     * @throws IllegalArgumentException Unsupported date type
     */
    private Instant toInstant(Object dateObject) {
        return switch (dateObject) {
            case LocalDate localDate -> localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            case LocalDateTime localDateTime -> localDateTime.atZone(ZoneId.systemDefault()).toInstant();
            case ZonedDateTime zonedDateTime -> zonedDateTime.toInstant();
            case Date date -> date.toInstant();
            case null, default -> throw new IllegalArgumentException("Unsupported date type: " + dateObject);
        };
    }
}