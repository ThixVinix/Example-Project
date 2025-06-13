package com.example.exampleproject.configs.annotations.validators.base;

import com.example.exampleproject.utils.MessageUtils;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * @return true if the list size is valid, false otherwise
     */
    protected boolean validateMaxSize(List<?> list,
                                      int maxSize,
                                      ConstraintValidatorContext context,
                                      String messageKey) {
        if (list.size() > maxSize) {
            addConstraintViolation(context, messageKey, String.valueOf(maxSize));
            return false;
        }
        return true;
    }

    /**
     * Validates that the list does not contain duplicate items.
     *
     * @param list the list to validate
     * @param context the validation context
     * @param messageKey the message key for the error message
     * @return true if the list does not contain duplicates, false otherwise
     */
    protected <T> boolean validateNoDuplicates(List<T> list, ConstraintValidatorContext context, String messageKey) {
        Set<T> uniqueItems = new HashSet<>();
        
        for (T item : list) {
            if (item != null && !uniqueItems.add(item)) {
                addConstraintViolation(context, messageKey);
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