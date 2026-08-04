package io.github.thixvinix.commons.i18n;

/**
 * What {@link Messages#get(String, Object...)} does when a key resolves to nothing.
 */
public enum MissingMessagePolicy {

    /**
     * Return the key itself and log a one-time warning. Safe default for production: a missing
     * translation degrades to an ugly-but-visible string instead of silently swallowing the error.
     */
    RETURN_KEY,

    /**
     * Throw {@link MissingMessageException}. Recommended for tests and CI, enabled by default via
     * the {@code -Dcommons.i18n.strict=true} system property.
     */
    THROW
}
