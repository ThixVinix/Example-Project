package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractListValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class Base64FileListValidator extends AbstractListValidator
        implements ConstraintValidator<Base64FileValidation, List<String>> {

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

        if (validateMaxSize(values, maxFileCount, context, 
                "msg.validation.request.field.base64file.max.file.count")) {
            return false;
        }

        if (!validateNoDuplicates(values, context)) {
            return false;
        }

        return validateEachItem(values, this::validateBase64File, context,
                "msg.validation.request.field.base64file.invalid.list");
    }

    private boolean validateBase64File(String value, ConstraintValidatorContext context) {
        return base64FileValidator.isValid(value, context);
    }
}
