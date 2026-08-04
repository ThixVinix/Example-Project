package io.github.thixvinix.commons.validation;

import jakarta.validation.ConstraintValidatorContext;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToLongFunction;

import static java.util.Objects.nonNull;

/**
 * Abstract base class for validators that validate lists of items.
 */
@Slf4j
public abstract class AbstractListValidator extends AbstractValidator {

    /**
     * Validates that the list does not exceed the maximum size.
     *
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
     * @return true if the list contains duplicates, false otherwise
     */
    protected <T> boolean hasDuplicateItems(List<T> list, ConstraintValidatorContext context, String messageKey) {
        Set<T> uniqueItems = new HashSet<>();

        for (T item : list) {
            if (nonNull(item) && !uniqueItems.add(item)) {
                addConstraintViolation(context, messageKey);
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if any item in the list is invalid using the provided item validator.
     *
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
     * @return true if the total size of the items exceeds the maximum allowed size, false otherwise
     */
    protected <T> boolean validateTotalSize(List<T> list,
                                            int maxTotalSizeMB,
                                            ToLongFunction<T> sizeCalculator,
                                            ConstraintValidatorContext context,
                                            String messageKey) {

        int normalizedMaxTotalSizeMB = validateMaxTotalSizeMB(maxTotalSizeMB);
        long maxTotalSizeInBytes = normalizedMaxTotalSizeMB * BYTES_IN_ONE_MB;
        long totalSizeInBytes = 0L;

        for (T item : list) {
            if (nonNull(item)) {
                totalSizeInBytes += sizeCalculator.applyAsLong(item);
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
