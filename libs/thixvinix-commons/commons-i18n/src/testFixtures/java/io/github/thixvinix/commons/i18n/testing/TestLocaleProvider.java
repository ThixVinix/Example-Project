package io.github.thixvinix.commons.i18n.testing;

import io.github.thixvinix.commons.i18n.LocaleProvider;

import java.util.Locale;

public final class TestLocaleProvider implements LocaleProvider {

    @Override
    public Locale currentLocale() {
        return TestLocale.get();
    }

    @Override
    public int order() {
        return 1000;
    }
}
