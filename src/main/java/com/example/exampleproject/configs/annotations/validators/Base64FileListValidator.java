package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Base64FileListValidator implements ConstraintValidator<Base64FileValidation, List<String>> {

    private Base64FileValidator base64FileValidator;
    private int maxFileCount;

    @Override
    public void initialize(Base64FileValidation annotation) {
        this.maxFileCount = annotation.maxFileCount();
        base64FileValidator = new Base64FileValidator();
        base64FileValidator.initialize(annotation);
    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        if (isNullOrEmpty(values)) {
            return true;
        }

        if (values.size() > maxFileCount) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            MessageUtils.getMessage(
                                    "msg.validation.request.field.base64file.max.file.count", maxFileCount)
                    )
                    .addConstraintViolation();
            return false;
        }

        Set<String> uniqueFiles = new HashSet<>();

        for (int i = 0; i < values.size(); i++) {
            String value = values.get(i);

            if (!uniqueFiles.add(value)) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.base64file.duplicate.file")
                        )
                        .addConstraintViolation();
                return false;
            }

            if (!base64FileValidator.isValid(value, context)) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                MessageUtils.getMessage("msg.validation.request.field.base64file.invalid.list",
                                        i + 1)
                        )
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }

    private boolean isNullOrEmpty(List<String> values) {
        return values == null || values.isEmpty();
    }


}