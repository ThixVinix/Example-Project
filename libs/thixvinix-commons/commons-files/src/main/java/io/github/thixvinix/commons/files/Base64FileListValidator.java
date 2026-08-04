package io.github.thixvinix.commons.files;

import io.github.thixvinix.commons.validation.AbstractListValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Validator for {@link Base64FileValidation} on a {@code List<String>} field: validates the
 * list's size, total size, uniqueness, and each item individually.
 */
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

        return !hasValidationErrors(values, context);
    }

    private boolean hasValidationErrors(List<String> values, ConstraintValidatorContext context) {
        return validateMaxSize(values, helper.getMaxFileCount(), context, FileMessageKeys.BASE64_MAX_FILE_COUNT)
                || validateTotalSize(values, helper.getMaxTotalSizeInMB(), helper::calculateBase64FileSize, context,
                FileMessageKeys.BASE64_MAX_TOTAL_SIZE)
                || hasDuplicateItems(values, context, FileMessageKeys.BASE64_DUPLICATE_FILE)
                || hasInvalidItem(values, helper::validateIndividualBase64File, context,
                FileMessageKeys.BASE64_INVALID_LIST_ITEM);
    }
}
