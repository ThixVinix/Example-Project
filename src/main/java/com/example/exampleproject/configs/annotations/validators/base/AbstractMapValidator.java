package com.example.exampleproject.configs.annotations.validators.base;

import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
import java.util.function.ToLongFunction;

import static java.util.Objects.nonNull;

/**
 * Abstract base class for validators that validate maps of items.
 * <p>
 * This class provides common methods for validating maps, such as checking for
 * maximum size and total size of items.
 */
@Slf4j
public abstract class AbstractMapValidator extends AbstractValidator {

    /**
     * Validates that the map does not exceed the maximum size.
     *
     * @param map     the map to validate
     * @param maxSize the maximum allowed size
     * @param context the validation context
     * @return true if validation fails (map size exceeds max size), false otherwise
     */
    protected boolean validateMaxSize(Map<?, ?> map,
                                      int maxSize,
                                      ConstraintValidatorContext context) {
        if (map.size() > maxSize) {
            addConstraintViolation(context,
                    "msg.validation.request.field.base64file.max.file.count", String.valueOf(maxSize));
            return true;
        }
        return false;
    }

    /**
     * Validates that the total size of the items in the map does not exceed a specified maximum size in MB.
     *
     * @param <K>            the type of keys in the map
     * @param <V>            the type of values in the map
     * @param map            the map of items to validate
     * @param maxTotalSizeMB the maximum allowed total size in megabytes; if invalid, a default value is used
     * @param sizeCalculator the strategy for calculating the size of individual items in bytes
     * @param context        the validation context used to report constraint violations
     * @return true if the total size of the items exceeds the maximum allowed size, false otherwise
     */
    protected <K, V> boolean validateTotalSize(Map<K, V> map,
                                               int maxTotalSizeMB,
                                               ToLongFunction<V> sizeCalculator,
                                               ConstraintValidatorContext context) {

        maxTotalSizeMB = validateMaxTotalSizeMB(maxTotalSizeMB);
        long maxTotalSizeInBytes = maxTotalSizeMB * BYTES_IN_ONE_MB;
        long totalSizeInBytes = NumberUtils.LONG_ZERO;

        for (V value : map.values()) {
            if (nonNull(value)) {
                long itemSize = sizeCalculator.applyAsLong(value);
                totalSizeInBytes += itemSize;
            }
        }

        if (totalSizeInBytes > maxTotalSizeInBytes) {
            double actualTotalSizeInMB = (double) totalSizeInBytes / BYTES_IN_ONE_MB;
            addConstraintViolation(context, "msg.validation.request.field.base64file.max.total.size",
                    String.format("%.4f", actualTotalSizeInMB),
                    String.valueOf(maxTotalSizeMB));
            return true;
        }
        return false;
    }
}