package io.github.thixvinix.commons.i18n;

import java.util.Locale;

/**
 * Registered via {@code META-INF/services} to prove {@link Messages} discovers a
 * {@link LocaleProvider} without any manual wiring, honoring {@link #order()}.
 */
public final class FixtureLocaleProvider implements LocaleProvider {

    @Override
    public Locale currentLocale() {
        return Locale.forLanguageTag("pt-BR");
    }

    @Override
    public int order() {
        return 1000;
    }
}
