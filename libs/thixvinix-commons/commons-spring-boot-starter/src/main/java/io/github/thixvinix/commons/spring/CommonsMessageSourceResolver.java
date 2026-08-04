package io.github.thixvinix.commons.spring;

import io.github.thixvinix.commons.i18n.MessageResolver;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;
import java.util.Optional;

/**
 * Adapts the application's Spring {@link MessageSource} into a {@link MessageResolver}, so a
 * consumer's own {@code messages_*.properties} can override any thixvinix-commons key by simply
 * declaring it — no extra wiring beyond adding this starter.
 */
public final class CommonsMessageSourceResolver implements MessageResolver {

    private final MessageSource messageSource;

    public CommonsMessageSourceResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public Optional<String> resolve(String key, Locale locale, Object... args) {
        try {
            return Optional.of(messageSource.getMessage(key, args, locale));
        } catch (NoSuchMessageException e) {
            return Optional.empty();
        }
    }

    @Override
    public int order() {
        return 100;
    }
}
