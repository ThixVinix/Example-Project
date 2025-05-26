package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64ImageValidation;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Validator to check if a string is a valid base64 encoded image.
 */
@Slf4j
public class Base64ImageValidator implements ConstraintValidator<Base64ImageValidation, String> {

    private static final Pattern BASE64_IMAGE_PATTERN = Pattern.compile("^data:image/[a-zA-Z]+;base64,.*");

    @Override
    public void initialize(Base64ImageValidation annotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        if (!BASE64_IMAGE_PATTERN.matcher(value).matches()) {
            addConstraintViolation(context, "msg.validation.request.field.base64image.invalid.format");
            return false;
        }

        String base64Content = value.substring(value.indexOf(",") + 1);
        
        try {
            Base64.getDecoder().decode(base64Content);
            return true;
        } catch (IllegalArgumentException e) {
            log.debug("Invalid base64 content: {}", e.getMessage());
            addConstraintViolation(context, "msg.validation.request.field.base64image.invalid.content");
            return false;
        }
    }

    /**
     * Adds a custom validation message.
     *
     * @param context validation context
     * @param messageKey the message key to use
     */
    private void addConstraintViolation(ConstraintValidatorContext context, String messageKey) {
        context.disableDefaultConstraintViolation();
        
        String message = MessageUtils.getMessage(messageKey);
        
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}