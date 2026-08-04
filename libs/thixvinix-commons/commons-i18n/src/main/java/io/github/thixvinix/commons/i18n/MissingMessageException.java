package io.github.thixvinix.commons.i18n;

/**
 * Thrown by {@link Messages} when {@link MissingMessagePolicy#THROW} is active and a key cannot
 * be resolved by any registered {@link MessageResolver}.
 */
public class MissingMessageException extends RuntimeException {

    public MissingMessageException(String key) {
        super("No message found for key '" + key + "'");
    }
}
