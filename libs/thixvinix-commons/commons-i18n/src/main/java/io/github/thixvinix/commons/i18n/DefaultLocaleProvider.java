package io.github.thixvinix.commons.i18n;

import java.util.Locale;

/**
 * Fallback {@link LocaleProvider} used when no other provider is registered on the classpath.
 */
public final class DefaultLocaleProvider implements LocaleProvider {

    @Override
    public Locale currentLocale() {
        return Locale.getDefault();
    }
}
