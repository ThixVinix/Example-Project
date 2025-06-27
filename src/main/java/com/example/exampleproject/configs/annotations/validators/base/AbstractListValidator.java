package com.example.exampleproject.configs.annotations.validators.base;

import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * @param list the list to validate
     * @param maxSize the maximum allowed size
     * @param context the validation context
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
     * Validates that the list does not contain duplicate items.
     *
     * @param list    the list to validate
     * @param context the validation context
     * @return true if the list does not contain duplicates, false otherwise
     */
    protected <T> boolean validateNoDuplicates(List<T> list, ConstraintValidatorContext context) {
        Set<T> uniqueItems = new HashSet<>();

        for (T item : list) {
            if (nonNull(item) && !uniqueItems.add(item)) {
                addConstraintViolation(context, "msg.validation.request.field.base64file.duplicate.file");
                return false;
            }
        }

        return true;
    }

    /**
     * Validates each item in the list using the provided item validator.
     *
     * @param list the list to validate
     * @param itemValidator the validator for individual items
     * @param context the validation context
     * @param invalidItemMessageKey the message key for invalid item error messages
     * @return true if all items are valid, false otherwise
     */
    protected <T> boolean validateEachItem(List<T> list, ItemValidator<T> itemValidator, 
                                          ConstraintValidatorContext context, 
                                          String invalidItemMessageKey) {
        for (int i = 0; i < list.size(); i++) {
            T item = list.get(i);

            if (!itemValidator.isValid(item, context)) {
                addConstraintViolation(context, invalidItemMessageKey, String.valueOf(i + 1));
                return false;
            }
        }

        return true;
    }

    /**
     * Validates that the total size of the items in the list does not exceed a specified maximum size in MB.
     *
     * @param <T> the type of elements in the list
     * @param list the list of items to validate
     * @param maxTotalSizeMB the maximum allowed total size in megabytes; if invalid, a default value is used
     * @param sizeCalculator the strategy for calculating the size of individual items in bytes
     * @param context the validation context used to report constraint violations
     * @param messageKey the key for the error message in case of a violation
     * @return true if the total size of the items exceeds the maximum allowed size, false otherwise
     */
    protected <T> boolean validateTotalSize(List<T> list,
                                           int maxTotalSizeMB,
                                           SizeCalculator<T> sizeCalculator,
                                           ConstraintValidatorContext context,
                                           String messageKey) {

        final int DEFAULT_MAX_TOTAL_SIZE_IN_MB = 10;

        if (maxTotalSizeMB <= NumberUtils.INTEGER_ZERO) {
            log.warn("The value of maxTotalSizeMB provided is invalid ({}). Default value of {} MB will be used.",
                    maxTotalSizeMB, DEFAULT_MAX_TOTAL_SIZE_IN_MB);
            maxTotalSizeMB = DEFAULT_MAX_TOTAL_SIZE_IN_MB;
        }

        final long BYTES_IN_ONE_MB = 1024L * 1024L;
        long maxTotalSizeInBytes = maxTotalSizeMB * BYTES_IN_ONE_MB;
        long totalSizeInBytes = NumberUtils.LONG_ZERO;

        for (T item : list) {
            if (nonNull(item)) {
                long itemSize = sizeCalculator.calculateSize(item);
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

    /**
     * Interface for calculating the size of individual items.
     *
     * @param <T> the type of item to calculate size for
     */
    @FunctionalInterface
    public interface SizeCalculator<T> {
        /**
         * Calculates the size of an item in bytes.
         *
         * @param item the item to calculate size for
         * @return the size in bytes
         */
        long calculateSize(T item);
    }

    /**
     * Interface for validating individual items in a list.
     *
     * @param <T> the type of item to validate
     */
    @FunctionalInterface
    public interface ItemValidator<T> {
        /**
         * Validates an individual item.
         *
         * @param item the item to validate
         * @param context the validation context
         * @return true if the item is valid, false otherwise
         */
        boolean isValid(T item, ConstraintValidatorContext context);
    }
}
