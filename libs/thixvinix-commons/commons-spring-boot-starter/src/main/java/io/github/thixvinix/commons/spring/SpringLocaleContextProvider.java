package io.github.thixvinix.commons.spring;

import io.github.thixvinix.commons.i18n.LocaleProvider;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * Binds {@code io.github.thixvinix.commons.i18n.Messages}' notion of "current locale" to Spring's
 * {@link LocaleContextHolder}, so it tracks whatever {@code LocaleResolver} the application uses.
 */
public final class SpringLocaleContextProvider implements LocaleProvider {

    @Override
    public Locale currentLocale() {
        return LocaleContextHolder.getLocale();
    }

    @Override
    public int order() {
        return 100;
    }
}
