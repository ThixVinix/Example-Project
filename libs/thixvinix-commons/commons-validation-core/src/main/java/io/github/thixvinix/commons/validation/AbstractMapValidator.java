package io.github.thixvinix.commons.validation;

import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;
import java.util.Map;
import java.util.function.ToLongFunction;

import static java.util.Objects.nonNull;

/**
 * Abstract base class for validators that validate maps of items.
 */
@Slf4j
public abstract class AbstractMapValidator extends AbstractValidator {

    /**
     * Validates that the map does not exceed the maximum size.
     *
     * @return true if validation fails (map size exceeds max size), false otherwise
     */
    protected boolean validateMaxSize(Map<?, ?> map,
                                      int maxSize,
                                      ConstraintValidatorContext context,
                                      String messageKey) {
        if (map.size() > maxSize) {
            addConstraintViolation(context, messageKey, String.valueOf(maxSize));
            return true;
        }
        return false;
    }

    /**
     * Validates that the total size of the items in the map does not exceed a specified maximum size in MB.
     *
     * @return true if the total size of the items exceeds the maximum allowed size, false otherwise
     */
    protected <K, V> boolean validateTotalSize(Map<K, V> map,
                                               int maxTotalSizeMB,
                                               ToLongFunction<V> sizeCalculator,
                                               ConstraintValidatorContext context,
                                               String messageKey) {

        int normalizedMaxTotalSizeMB = validateMaxTotalSizeMB(maxTotalSizeMB);
        long maxTotalSizeInBytes = normalizedMaxTotalSizeMB * BYTES_IN_ONE_MB;
        long totalSizeInBytes = 0L;

        for (V value : map.values()) {
            if (nonNull(value)) {
                totalSizeInBytes += sizeCalculator.applyAsLong(value);
            }
        }

        if (totalSizeInBytes > maxTotalSizeInBytes) {
            double actualTotalSizeInMB = (double) totalSizeInBytes / BYTES_IN_ONE_MB;
            addConstraintViolation(context, messageKey,
                    String.format(Locale.US, "%.4f", actualTotalSizeInMB),
                    String.valueOf(normalizedMaxTotalSizeMB));
            return true;
        }
        return false;
    }
}
