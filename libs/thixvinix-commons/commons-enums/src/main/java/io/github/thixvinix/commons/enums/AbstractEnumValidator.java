package io.github.thixvinix.commons.enums;

import io.github.thixvinix.commons.validation.AbstractValidator;

import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * Abstract base class for validators that validate enum values, either against a
 * {@code getValue()}/{@code getCode()} accessor or the enum constant's literal name.
 */
@Slf4j
public abstract class AbstractEnumValidator extends AbstractValidator {

    protected Class<? extends Enum<?>> enumClass;
    protected Method accessorMethod;
    protected String methodName;
    protected boolean hideValidOptions;

    /**
     * Initializes the validator with the enum class and accessor method name.
     *
     * @param enumClass        the enum class to validate against
     * @param methodName       the name of the accessor method (e.g., "getValue", "getCode")
     * @param hideValidOptions whether to hide valid options in error messages
     */
    protected void initialize(Class<? extends Enum<?>> enumClass, String methodName, boolean hideValidOptions) {
        this.enumClass = enumClass;
        this.methodName = methodName;
        this.hideValidOptions = hideValidOptions;

        try {
            this.accessorMethod = enumClass.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            log.warn("The Enum {} does not contain the required '{}' method.",
                    enumClass.getSimpleName(), methodName, e);
            this.accessorMethod = null;
        }
    }

    /**
     * Adds a constraint violation with the appropriate error message based on the
     * hideValidOptions setting.
     *
     * @param context                  the validation context
     * @param invalidValue             the invalid value
     * @param messageKeyWithOptions    the message key for an error message with options
     * @param messageKeyWithoutOptions the message key for an error message without options
     */
    protected void addConstraintViolationForEnum(ConstraintValidatorContext context,
                                                 Object invalidValue,
                                                 String messageKeyWithOptions,
                                                 String messageKeyWithoutOptions) {
        if (hideValidOptions) {
            addConstraintViolation(context, messageKeyWithoutOptions, String.valueOf(invalidValue));
        } else {
            addConstraintViolation(context, messageKeyWithOptions, String.valueOf(invalidValue), getValidValuesAsString());
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
        if (nonNull(accessorMethod)) {
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
     * @param value        the value to check
     * @return true if the value matches the enum value, false otherwise
     */
    protected boolean enumValueMatches(Enum<?> enumConstant, Object value) {
        if (nonNull(accessorMethod)) {
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
