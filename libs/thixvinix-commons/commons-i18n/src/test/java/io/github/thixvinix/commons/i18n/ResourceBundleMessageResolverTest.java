package io.github.thixvinix.commons.i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ResourceBundleMessageResolver Tests")
class ResourceBundleMessageResolverTest {

    private static final String BASENAME = "io/github/thixvinix/commons/i18n/fixture/messages";

    private final ResourceBundleMessageResolver resolver = new ResourceBundleMessageResolver(List.of(BASENAME));

    private Locale defaultLocale;

    @BeforeEach
    void saveDefaultLocale() {
        defaultLocale = Locale.getDefault();
    }

    @AfterEach
    void restoreDefaultLocale() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    @DisplayName("Resolves and formats a message for an exact locale match")
    void resolvesExactLocaleWithArgs() {
        Optional<String> result = resolver.resolve("fixture.greeting", Locale.forLanguageTag("pt-BR"), "Maria");

        assertThat(result).contains("Olá, Maria!");
    }

    @Test
    @DisplayName("Returns the raw text unformatted when no arguments are given")
    void returnsRawTextWithoutArgs() {
        Optional<String> result = resolver.resolve("fixture.rootOnly", Locale.ROOT);

        assertThat(result).contains("Root fallback text.");
    }

    @Test
    @DisplayName("Falls back within the requested locale's own hierarchy (en_US -> en -> root)")
    void fallsBackWithinRequestedLocaleHierarchy() {
        Optional<String> result = resolver.resolve("fixture.enOnly", Locale.US);

        assertThat(result).contains("English-specific text.");
    }

    @Test
    @DisplayName("Never falls back to the JVM default locale when the requested locale has no match")
    void doesNotLeakToJvmDefaultLocale() {
        Locale.setDefault(Locale.forLanguageTag("pt-BR"));

        Optional<String> result = resolver.resolve("fixture.enOnly", Locale.JAPAN);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Locale.ROOT still resolves keys present only in the no-suffix bundle")
    void rootLocaleResolvesRootBundle() {
        Optional<String> result = resolver.resolve("fixture.rootOnly", Locale.ROOT);

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("Returns empty for an unknown key")
    void returnsEmptyForUnknownKey() {
        Optional<String> result = resolver.resolve("fixture.doesNotExist", Locale.US);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Returns empty when the basename itself does not exist")
    void returnsEmptyForUnknownBasename() {
        ResourceBundleMessageResolver missingBasename =
                new ResourceBundleMessageResolver(List.of("io/github/thixvinix/commons/i18n/fixture/doesNotExist"));

        assertThat(missingBasename.resolve("anything", Locale.US)).isEmpty();
    }
}
