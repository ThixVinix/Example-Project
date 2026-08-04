package io.github.thixvinix.commons.i18n;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Static facade used by every {@code ConstraintValidator} to resolve a localized message.
 * <p>
 * It is deliberately static: Hibernate Validator instantiates constraint validators with a
 * no-argument constructor, so there is no injection point on that code path in a plain
 * Jakarta Validation consumer. Unlike the {@code MessageUtils} pattern this replaces, there is no
 * required initialization order — the first call lazily builds a working default resolver
 * (discovered via {@link java.util.ServiceLoader}), so it never depends on a framework lifecycle
 * callback having already run.
 * <p>
 * {@link #setResolver(MessageResolver)} / {@link #setLocaleProvider(LocaleProvider)} let a
 * framework integration (e.g. the Spring Boot starter) or a test replace the defaults; {@link #reset()}
 * restores {@code ServiceLoader} discovery.
 */
public final class Messages {

    private static final Logger log = LoggerFactory.getLogger(Messages.class);

    private static final String OVERLAY_BASENAME = "commons-messages";

    private static final Set<String> WARNED_KEYS = ConcurrentHashMap.newKeySet();

    private static volatile MessageResolver resolver;
    private static volatile LocaleProvider localeProvider;
    private static volatile MissingMessagePolicy missingMessagePolicy = initialMissingMessagePolicy();

    private Messages() {
    }

    /**
     * Resolves {@code key} using the current {@link LocaleProvider}.
     */
    public static String get(String key, Object... args) {
        return get(currentLocale(), key, args);
    }

    /**
     * Resolves {@code key} for an explicit locale, bypassing the {@link LocaleProvider}.
     */
    public static String get(Locale locale, String key, Object... args) {
        Optional<String> resolved = find(key, locale, args);
        return resolved.orElseGet(() -> handleMissing(key));
    }

    /**
     * Resolves {@code key}, returning empty instead of applying the missing-message policy.
     */
    public static Optional<String> find(String key, Locale locale, Object... args) {
        return resolver().resolve(key, locale, args);
    }

    public static void setResolver(MessageResolver custom) {
        resolver = custom;
    }

    public static void setLocaleProvider(LocaleProvider custom) {
        localeProvider = custom;
    }

    public static void setMissingMessagePolicy(MissingMessagePolicy policy) {
        missingMessagePolicy = policy;
    }

    /**
     * Restores {@code ServiceLoader}-discovered defaults. Intended for test teardown.
     */
    public static synchronized void reset() {
        resolver = null;
        localeProvider = null;
        missingMessagePolicy = initialMissingMessagePolicy();
        WARNED_KEYS.clear();
    }

    static MessageResolver resolver() {
        MessageResolver current = resolver;
        if (current == null) {
            synchronized (Messages.class) {
                current = resolver;
                if (current == null) {
                    current = buildDefaultResolver();
                    resolver = current;
                }
            }
        }
        return current;
    }

    private static Locale currentLocale() {
        LocaleProvider current = localeProvider;
        if (current == null) {
            synchronized (Messages.class) {
                current = localeProvider;
                if (current == null) {
                    current = buildDefaultLocaleProvider();
                    localeProvider = current;
                }
            }
        }
        return current.currentLocale();
    }

    private static String handleMissing(String key) {
        if (missingMessagePolicy == MissingMessagePolicy.THROW) {
            throw new MissingMessageException(key);
        }
        if (WARNED_KEYS.add(key)) {
            log.warn("No message found for key '{}'; falling back to the key itself.", key);
        }
        return key;
    }

    private static MessageResolver buildDefaultResolver() {
        List<String> basenames = new ArrayList<>();
        basenames.add(OVERLAY_BASENAME);
        ServiceLoader.load(MessageBundleContribution.class)
                .forEach(contribution -> basenames.addAll(contribution.basenames()));

        List<MessageResolver> resolvers = new ArrayList<>();
        ServiceLoader.load(MessageResolver.class).forEach(resolvers::add);
        resolvers.add(new ResourceBundleMessageResolver(basenames));

        return new CompositeMessageResolver(resolvers);
    }

    private static LocaleProvider buildDefaultLocaleProvider() {
        return ServiceLoader.load(LocaleProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .max(Comparator.comparingInt(LocaleProvider::order))
                .orElseGet(DefaultLocaleProvider::new);
    }

    private static MissingMessagePolicy initialMissingMessagePolicy() {
        return Boolean.getBoolean("commons.i18n.strict") ? MissingMessagePolicy.THROW : MissingMessagePolicy.RETURN_KEY;
    }
}
