package io.github.thixvinix.commons.validation.support;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Resolves the externally-visible name of a field (e.g. its {@code @JsonProperty} or Spring
 * {@code @BindParam} name) for use in validation messages. Discovered via
 * {@link java.util.ServiceLoader}; the highest {@link #order()} wins.
 */
public interface PropertyNameProvider {

    Optional<String> nameFor(Class<?> owner, Field field);

    default int order() {
        return 0;
    }
}
