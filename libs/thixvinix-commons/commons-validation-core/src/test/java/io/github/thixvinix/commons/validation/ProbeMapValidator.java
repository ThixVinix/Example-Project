package io.github.thixvinix.commons.validation;

import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;
import java.util.function.ToLongFunction;

class ProbeMapValidator extends AbstractMapValidator {

    boolean maxSize(Map<?, ?> map, int maxSize, ConstraintValidatorContext context, String messageKey) {
        return validateMaxSize(map, maxSize, context, messageKey);
    }

    <K, V> boolean totalSize(Map<K, V> map, int maxTotalSizeMB, ToLongFunction<V> sizeCalculator,
                              ConstraintValidatorContext context, String messageKey) {
        return validateTotalSize(map, maxTotalSizeMB, sizeCalculator, context, messageKey);
    }
}
