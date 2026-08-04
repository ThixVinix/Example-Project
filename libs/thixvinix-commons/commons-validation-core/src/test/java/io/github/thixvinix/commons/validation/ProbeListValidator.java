package io.github.thixvinix.commons.validation;

import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.ToLongFunction;

class ProbeListValidator extends AbstractListValidator {

    boolean maxSize(List<?> list, int maxSize, ConstraintValidatorContext context, String messageKey) {
        return validateMaxSize(list, maxSize, context, messageKey);
    }

    <T> boolean duplicates(List<T> list, ConstraintValidatorContext context, String messageKey) {
        return hasDuplicateItems(list, context, messageKey);
    }

    <T> boolean invalidItem(List<T> list, BiPredicate<T, ConstraintValidatorContext> itemValidator,
                             ConstraintValidatorContext context, String messageKey) {
        return hasInvalidItem(list, itemValidator, context, messageKey);
    }

    <T> boolean totalSize(List<T> list, int maxTotalSizeMB, ToLongFunction<T> sizeCalculator,
                           ConstraintValidatorContext context, String messageKey) {
        return validateTotalSize(list, maxTotalSizeMB, sizeCalculator, context, messageKey);
    }
}
