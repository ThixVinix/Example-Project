package io.github.thixvinix.commons.i18n.testing;

import io.github.thixvinix.commons.i18n.Messages;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension that installs {@link TestLocaleProvider} for the duration of the test class
 * and resets {@link Messages} to its {@code ServiceLoader}-discovered defaults afterwards, so
 * tests never leak global state into one another.
 */
public final class CommonsI18nExtension implements BeforeAllCallback, AfterEachCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        Messages.setLocaleProvider(new TestLocaleProvider());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        TestLocale.clear();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        Messages.reset();
    }
}
