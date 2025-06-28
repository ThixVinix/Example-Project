package com.example.exampleproject.configs.annotations.validators;

import com.example.exampleproject.configs.annotations.Base64FileValidation;
import com.example.exampleproject.configs.annotations.validators.base.AbstractListValidator;
import com.example.exampleproject.configs.annotations.validators.helpers.Base64FileCollectionValidatorHelper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Base64FileListValidator extends AbstractListValidator
        implements ConstraintValidator<Base64FileValidation, List<String>> {

    private Base64FileCollectionValidatorHelper helper;

    @Override
    public void initialize(Base64FileValidation annotation) {
        this.helper = new Base64FileCollectionValidatorHelper();
        this.helper.initialize(annotation);
    }

    @Override
    public boolean isValid(List<String> values, ConstraintValidatorContext context) {
        if (isNullOrEmpty(values)) {
            return true;
        }

        if (validateMaxSize(values, helper.getMaxFileCount(), context, 
                "msg.validation.request.field.base64file.max.file.count")) {
            return false;
        }

        if (validateTotalSize(values, helper.getMaxTotalSizeInMB(), helper::calculateBase64FileSize, context,
                "msg.validation.request.field.base64file.max.total.size")) {
            return false;
        }

        if (!validateNoDuplicates(values, context)) {
            return false;
        }

        return validateEachItem(values, helper::validateIndividualBase64File, context,
                "msg.validation.request.field.base64file.invalid.list");
    }
}
