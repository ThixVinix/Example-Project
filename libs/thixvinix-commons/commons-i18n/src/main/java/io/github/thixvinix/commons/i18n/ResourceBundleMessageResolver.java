package io.github.thixvinix.commons.i18n;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default {@link MessageResolver}, backed by plain JDK {@link ResourceBundle}s — no Spring, no
 * third-party dependency.
 * <p>
 * Each basename is expected to ship three files: {@code &lt;basename&gt;.properties} (root/English
 * fallback), {@code &lt;basename&gt;_en.properties} and {@code &lt;basename&gt;_pt_BR.properties}.
 * Lookup uses {@link ResourceBundle.Control#getNoFallbackControl(String)} deliberately: the
 * requested locale still falls back through its own hierarchy (e.g. {@code pt_BR -> pt -> root}),
 * but never further falls back to the JVM's {@link Locale#getDefault()} — which would otherwise
 * make a Portuguese-defaulted JVM silently answer Portuguese to an English request.
 */
public final class ResourceBundleMessageResolver implements MessageResolver {

    private final List<String> basenames;
    private final ClassLoader classLoader;
    private final ConcurrentHashMap<BundleKey, Optional<ResourceBundle>> cache = new ConcurrentHashMap<>();

    public ResourceBundleMessageResolver(List<String> basenames) {
        this(basenames, ResourceBundleMessageResolver.class.getClassLoader());
    }

    public ResourceBundleMessageResolver(List<String> basenames, ClassLoader classLoader) {
        this.basenames = List.copyOf(basenames);
        this.classLoader = classLoader;
    }

    @Override
    public Optional<String> resolve(String key, Locale locale, Object... args) {
        for (String basename : basenames) {
            Optional<ResourceBundle> bundle = bundleFor(basename, locale);
            if (bundle.isPresent() && bundle.get().containsKey(key)) {
                return Optional.of(format(bundle.get().getString(key), locale, args));
            }
        }
        return Optional.empty();
    }

    private String format(String raw, Locale locale, Object... args) {
        if (args == null || args.length == 0) {
            return raw;
        }
        return new MessageFormat(raw, locale).format(args);
    }

    private Optional<ResourceBundle> bundleFor(String basename, Locale locale) {
        return cache.computeIfAbsent(new BundleKey(basename, locale), this::loadBundle);
    }

    private Optional<ResourceBundle> loadBundle(BundleKey key) {
        try {
            return Optional.of(ResourceBundle.getBundle(
                    key.basename(),
                    key.locale(),
                    classLoader,
                    ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)));
        } catch (MissingResourceException e) {
            return Optional.empty();
        }
    }

    private record BundleKey(String basename, Locale locale) {
    }
}
