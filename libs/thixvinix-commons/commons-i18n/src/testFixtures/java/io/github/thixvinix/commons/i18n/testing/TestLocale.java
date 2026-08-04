package io.github.thixvinix.commons.i18n.testing;

import java.util.Locale;

/**
 * Thread-local locale used by {@link TestLocaleProvider}. Drop-in replacement for the
 * {@code LocaleContextHolder.setLocale(...)} save/restore pattern used throughout the original
 * validator tests: {@link #set(String)} accepts the same underscore-separated tags
 * (e.g. {@code "pt_BR"}, {@code "en"}) used in their {@code @CsvSource} matrices.
 */
public final class TestLocale {

    private static final ThreadLocal<Locale> CURRENT = ThreadLocal.withInitial(Locale::getDefault);

    private TestLocale() {
    }

    public static void set(Locale locale) {
        CURRENT.set(locale);
    }

    public static void set(String underscoreTag) {
        set(Locale.forLanguageTag(underscoreTag.replace('_', '-')));
    }

    public static Locale get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
