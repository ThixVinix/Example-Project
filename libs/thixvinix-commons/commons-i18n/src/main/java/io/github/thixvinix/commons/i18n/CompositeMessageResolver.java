package io.github.thixvinix.commons.i18n;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Consults a list of {@link MessageResolver}s in descending {@link MessageResolver#order()},
 * returning the first non-empty answer. This is what lets an application override a library
 * message: register a resolver (or an overlay bundle, see {@link Messages}) with a higher order
 * than the library's own.
 */
public final class CompositeMessageResolver implements MessageResolver {

    private final List<MessageResolver> resolvers;

    public CompositeMessageResolver(List<MessageResolver> resolvers) {
        this.resolvers = resolvers.stream()
                .sorted(Comparator.comparingInt(MessageResolver::order).reversed())
                .toList();
    }

    @Override
    public Optional<String> resolve(String key, Locale locale, Object... args) {
        for (MessageResolver resolver : resolvers) {
            Optional<String> result = resolver.resolve(key, locale, args);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
