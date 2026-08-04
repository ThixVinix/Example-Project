package io.github.thixvinix.commons.i18n;

import java.util.Locale;

/**
 * Supplies the "current" locale to {@link Messages} when no explicit locale is passed.
 * <p>
 * The default implementation returns {@link Locale#getDefault()}, appropriate for a batch job
 * or CLI with no request context. Web frameworks contribute their own provider (e.g. the Spring
 * starter binds this to {@code LocaleContextHolder.getLocale()}) via {@link java.util.ServiceLoader},
 * with the highest {@link #order()} winning.
 */
public interface LocaleProvider {

    Locale currentLocale();

    default int order() {
        return 0;
    }
}
