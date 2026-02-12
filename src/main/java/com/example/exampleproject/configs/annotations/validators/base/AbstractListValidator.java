package com.example.exampleproject.configs.annotations.validators.base;

import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToLongFunction;

import static java.util.Objects.nonNull;

/**
 * Abstract base class for validators that validate lists of items.
 * <p>
 * This class provides common methods for validating lists, such as checking for
 * maximum size and duplicate items.
 */
@Slf4j
public abstract class AbstractListValidator extends AbstractValidator {

    /**
     * Validates that the list does not exceed the maximum size.
     *
     * @param list       the list to validate
     * @param maxSize    the maximum allowed size
     * @param context    the validation context
     * @param messageKey the message key for the error message
     * @return true if validation fails (list size exceeds max size), false otherwise
     */
    protected boolean validateMaxSize(List<?> list,
                                      int maxSize,
                                      ConstraintValidatorContext context,
                                      String messageKey) {
        if (list.size() > maxSize) {
            addConstraintViolation(context, messageKey, String.valueOf(maxSize));
            return true;
        }
        return false;
    }

    /**
     * Checks if the list contains duplicate items.
     *
     * @param list    the list to validate
     * @param context the validation context
     * @return true if the list contains duplicates, false otherwise
     */
    protected <T> boolean hasDuplicateItems(List<T> list, ConstraintValidatorContext context) {
        Set<T> uniqueItems = new HashSet<>();

        for (T item : list) {
            if (nonNull(item) && !uniqueItems.add(item)) {
                addConstraintViolation(context, "msg.validation.request.field.base64file.duplicate.file");
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if any item in the list is invalid using the provided item validator.
     *
     * @param list                  the list to validate
     * @param itemValidator         the validator for individual items
     * @param context               the validation context
     * @param invalidItemMessageKey the message key for invalid item error messages
     * @return true if an invalid item is found, false otherwise
     */
    protected <T> boolean hasInvalidItem(List<T> list, BiPredicate<T, ConstraintValidatorContext> itemValidator,
                                         ConstraintValidatorContext context,
                                         String invalidItemMessageKey) {
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);

            if (!itemValidator.test(item, context)) {
                addConstraintViolation(context, invalidItemMessageKey, String.valueOf(i + 1));
                return true;
            }
        }

        return false;
    }

    /**
     * Validates that the total size of the items in the list does not exceed a specified maximum size in MB.
     *
     * @param <T>            the type of elements in the list
     * @param list           the list of items to validate
     * @param maxTotalSizeMB the maximum allowed total size in megabytes; if invalid, a default value is used
     * @param sizeCalculator the strategy for calculating the size of individual items in bytes
     * @param context        the validation context used to report constraint violations
     * @param messageKey     the key for the error message in case of a violation
     * @return true if the total size of the items exceeds the maximum allowed size, false otherwise
     */
    protected <T> boolean validateTotalSize(List<T> list,
                                            int maxTotalSizeMB,
                                            ToLongFunction<T> sizeCalculator,
                                            ConstraintValidatorContext context,
                                            String messageKey) {

        maxTotalSizeMB = validateMaxTotalSizeMB(maxTotalSizeMB);
        long maxTotalSizeInBytes = maxTotalSizeMB * BYTES_IN_ONE_MB;
        long totalSizeInBytes = NumberUtils.LONG_ZERO;

        for (T item : list) {
            if (nonNull(item)) {
                long itemSize = sizeCalculator.applyAsLong(item);
                totalSizeInBytes += itemSize;
            }
        }

        if (totalSizeInBytes > maxTotalSizeInBytes) {
            double actualTotalSizeInMB = (double) totalSizeInBytes / BYTES_IN_ONE_MB;
            addConstraintViolation(context, messageKey,
                    String.format("%.4f", actualTotalSizeInMB),
                    String.valueOf(maxTotalSizeMB));
            return true;
        }
        return false;
    }

}
