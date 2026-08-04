package io.github.thixvinix.commons.i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ServiceLoader Discovery Tests")
class ServiceLoaderDiscoveryTest {

    @BeforeEach
    void resetState() {
        Messages.reset();
    }

    @AfterEach
    void restoreState() {
        Messages.reset();
    }

    @Test
    @DisplayName("A MessageBundleContribution registered via META-INF/services is picked up with no manual wiring")
    void discoversContributedBundle() {
        assertThat(Messages.get(Locale.US, "fixture.greeting", "World")).isEqualTo("Hello, World!");
    }

    @Test
    @DisplayName("A LocaleProvider registered via META-INF/services is used when no locale is given explicitly")
    void discoversLocaleProvider() {
        assertThat(Messages.get("fixture.greeting", "Mundo")).isEqualTo("Olá, Mundo!");
    }
}
