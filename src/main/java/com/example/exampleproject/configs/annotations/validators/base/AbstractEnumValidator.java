package com.example.exampleproject.configs.annotations.validators.base;

import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Abstract base class for validators that validate enum values.
 * <p>
 * This class provides common methods for validating enum values, such as
 * checking if a value matches an enum constant.
 */
@Slf4j
public abstract class AbstractEnumValidator extends AbstractValidator {

    protected Class<? extends Enum<?>> enumClass;
    protected Method accessorMethod;
    protected String methodName;

    /**
     * Initializes the validator with the enum class and accessor method name.
     *
     * @param enumClass the enum class to validate against
     * @param methodName the name of the accessor method (e.g., "getValue", "getCode")
     */
    protected void initialize(Class<? extends Enum<?>> enumClass, String methodName) {
        this.enumClass = enumClass;
        this.methodName = methodName;

        try {
            this.accessorMethod = enumClass.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            log.warn("The Enum {} does not contain the required '{}' method.",
                    enumClass.getSimpleName(), methodName, e);
            this.accessorMethod = null;
        }
    }

    /**
     * Adds a constraint violation with a list of valid values.
     *
     * @param context the validation context
     * @param invalidValue the invalid value
     * @param messageKey the message key for the error message
     */
    protected void addConstraintViolationWithValidValues(ConstraintValidatorContext context, 
                                                        Object invalidValue, 
                                                        String messageKey) {
        try {
            context.disableDefaultConstraintViolation();

            String validValues = getValidValuesAsString();

            String message = MessageUtils.getMessage(
                    messageKey,
                    String.valueOf(invalidValue),
                    validValues
            );

            var builder = context.buildConstraintViolationWithTemplate(message);
            if (builder != null) {
                builder.addConstraintViolation();
            } else {
                log.warn("Could not build constraint violation for value: {}", invalidValue);
            }
        } catch (Exception e) {
            log.error("Error adding constraint violation for value {}: {}", invalidValue, e.getMessage(), e);
        }
    }

    /**
     * Gets a string representation of all valid values for the enum.
     *
     * @return a comma-separated string of valid values
     */
    protected String getValidValuesAsString() {
        return Arrays.stream(enumClass.getEnumConstants())
                .map(this::getEnumValue)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    /**
     * Gets the value of an enum constant using the accessor method.
     *
     * @param enumConstant the enum constant
     * @return the value of the enum constant
     */
    protected Object getEnumValue(Enum<?> enumConstant) {
        if (accessorMethod != null) {
            try {
                return accessorMethod.invoke(enumConstant);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Failed to access the '" + methodName + "' method of Enum " + enumClass.getSimpleName(), e);
            }
        } else {
            return enumConstant.name();
        }
    }

    /**
     * Checks if the provided value matches the enum value.
     *
     * @param enumConstant the enum constant
     * @param value the value to check
     * @return true if the value matches the enum value, false otherwise
     */
    protected boolean enumValueMatches(Enum<?> enumConstant, Object value) {
        if (accessorMethod != null) {
            try {
                Object enumValue = accessorMethod.invoke(enumConstant);
                return enumValue.equals(value);
            } catch (Exception e) {
                log.warn("Failed to access the '{}' method of Enum {}: {}", 
                        methodName, enumClass.getSimpleName(), e.getMessage());

                // Fall back to comparing with the enum name if method access fails
                return enumConstant.name().equals(value) || 
                       (value instanceof String string && enumConstant.name().equalsIgnoreCase(string));
            }
        } else {
            return enumConstant.name().equals(value) || 
                   (value instanceof String string && enumConstant.name().equalsIgnoreCase(string));
        }
    }
}
