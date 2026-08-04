package io.github.thixvinix.commons.i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Messages Tests")
class MessagesTest {

    @BeforeEach
    void resetState() {
        Messages.reset();
    }

    @AfterEach
    void restoreState() {
        Messages.reset();
    }

    @Test
    @DisplayName("get(locale, key, args) resolves through the injected resolver")
    void resolvesThroughInjectedResolver() {
        Messages.setResolver((key, locale, args) -> Optional.of("resolved:" + key));

        assertThat(Messages.get(Locale.US, "any.key")).isEqualTo("resolved:any.key");
    }

    @Test
    @DisplayName("get(key, args) uses the current LocaleProvider")
    void usesLocaleProviderWhenNoLocaleGiven() {
        Messages.setLocaleProvider(() -> Locale.forLanguageTag("pt-BR"));
        Messages.setResolver((key, locale, args) -> Optional.of(locale.toString()));

        assertThat(Messages.get("any.key")).isEqualTo("pt_BR");
    }

    @Test
    @DisplayName("RETURN_KEY policy yields the key itself for an unresolved message")
    void returnKeyPolicyReturnsTheKey() {
        Messages.setMissingMessagePolicy(MissingMessagePolicy.RETURN_KEY);
        Messages.setResolver((key, locale, args) -> Optional.empty());

        assertThat(Messages.get(Locale.US, "missing.key")).isEqualTo("missing.key");
    }

    @Test
    @DisplayName("THROW policy raises MissingMessageException for an unresolved message")
    void throwPolicyRaisesException() {
        Messages.setMissingMessagePolicy(MissingMessagePolicy.THROW);
        Messages.setResolver((key, locale, args) -> Optional.empty());

        assertThatThrownBy(() -> Messages.get(Locale.US, "missing.key"))
                .isInstanceOf(MissingMessageException.class)
                .hasMessageContaining("missing.key");
    }

    @Test
    @DisplayName("find(...) never applies the missing-message policy")
    void findNeverThrows() {
        Messages.setMissingMessagePolicy(MissingMessagePolicy.THROW);
        Messages.setResolver((key, locale, args) -> Optional.empty());

        assertThat(Messages.find("missing.key", Locale.US)).isEmpty();
    }

    @Test
    @DisplayName("The overlay basename 'commons-messages' takes priority over library-contributed bundles")
    void overlayBasenameOverridesLibraryBundles() {
        Messages.reset();

        Optional<String> overridden = Messages.find("commons.i18n.overlay.sample", Locale.US);

        assertThat(overridden).contains("Overlay wins.");
    }

    @Test
    @DisplayName("reset() discards the injected resolver and restores the strict policy set for library test runs")
    void resetRestoresDefaults() {
        Messages.setResolver((key, locale, args) -> Optional.of("stub"));
        Messages.setMissingMessagePolicy(MissingMessagePolicy.RETURN_KEY);
        Messages.reset();

        // The convention plugin runs library tests with -Dcommons.i18n.strict=true, so reset()
        // must restore THROW here rather than keep the RETURN_KEY set right before it.
        assertThatThrownBy(() -> Messages.get(Locale.US, "definitely.not.a.real.key"))
                .isInstanceOf(MissingMessageException.class);
    }
}
