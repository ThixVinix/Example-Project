package io.github.thixvinix.commons.i18n;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CompositeMessageResolver Tests")
class CompositeMessageResolverTest {

    @Test
    @DisplayName("Returns the first non-empty result in descending order of priority")
    void higherOrderWins() {
        MessageResolver low = fixed(0, "low-priority");
        MessageResolver high = fixed(100, "high-priority");

        CompositeMessageResolver composite = new CompositeMessageResolver(List.of(low, high));

        assertThat(composite.resolve("any.key", Locale.US)).contains("high-priority");
    }

    @Test
    @DisplayName("Falls through to the next resolver when the higher-priority one has no answer")
    void fallsThroughOnEmpty() {
        MessageResolver empty = (key, locale, args) -> Optional.empty();
        MessageResolver fallback = fixed(0, "fallback-value");

        CompositeMessageResolver composite = new CompositeMessageResolver(List.of(fallback, empty));

        assertThat(composite.resolve("any.key", Locale.US)).contains("fallback-value");
    }

    @Test
    @DisplayName("Returns empty when no resolver answers")
    void emptyWhenNoResolverAnswers() {
        CompositeMessageResolver composite = new CompositeMessageResolver(
                List.of((key, locale, args) -> Optional.empty()));

        assertThat(composite.resolve("any.key", Locale.US)).isEmpty();
    }

    private static MessageResolver fixed(int order, String value) {
        return new MessageResolver() {
            @Override
            public Optional<String> resolve(String key, Locale locale, Object... args) {
                return Optional.of(value);
            }

            @Override
            public int order() {
                return order;
            }
        };
    }
}
