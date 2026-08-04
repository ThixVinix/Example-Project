package io.github.thixvinix.commons.i18n;

import java.util.Locale;
import java.util.Optional;

/**
 * Resolves a message key, for a given locale, into localized text.
 * <p>
 * Implementations are discovered via {@link java.util.ServiceLoader} and combined by
 * {@link CompositeMessageResolver} according to {@link #order()} — the highest order wins.
 */
public interface MessageResolver {

    /**
     * Attempts to resolve {@code key} for {@code locale}, formatting the result with {@code args}
     * (via {@link java.text.MessageFormat}) when at least one argument is provided.
     *
     * @return the resolved message, or {@link Optional#empty()} if this resolver has no answer for the key
     */
    Optional<String> resolve(String key, Locale locale, Object... args);

    /**
     * Resolvers with a higher order are consulted first.
     */
    default int order() {
        return 0;
    }
}
