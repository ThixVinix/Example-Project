package io.github.thixvinix.commons.dates;

import io.github.thixvinix.commons.validation.support.PropertyNameProvider;
import io.github.thixvinix.commons.validation.support.ReflectionSupport;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * Resolves the display name to use for a field in a validation message: {@code @JsonProperty}
 * first, then any registered {@link PropertyNameProvider} (e.g. {@link ReflectivePropertyNameProvider}
 * for Spring's {@code @BindParam}), falling back to the field's own name.
 */
public final class PropertyNameResolver {

    private PropertyNameResolver() {
    }

    /**
     * Looks up {@code fieldName} on {@code owner} (walking up the superclass chain) and resolves
     * its display name. Returns {@code fieldName} unchanged if no such field is declared.
     */
    public static String resolveDisplayName(Class<?> owner, String fieldName) {
        Field field = ReflectionSupport.findFieldRecursive(owner, fieldName);
        if (field == null) {
            return fieldName;
        }
        return resolveDisplayName(owner, field);
    }

    private static String resolveDisplayName(Class<?> owner, Field field) {
        Optional<String> jsonPropertyName = jsonPropertyName(field);
        if (jsonPropertyName.isPresent()) {
            return jsonPropertyName.get();
        }

        for (PropertyNameProvider provider : providers()) {
            Optional<String> name = provider.nameFor(owner, field);
            if (name.isPresent()) {
                return name.get();
            }
        }

        return field.getName();
    }

    private static Optional<String> jsonPropertyName(Field field) {
        JsonProperty annotation = field.getAnnotation(JsonProperty.class);
        if (annotation == null || annotation.value().isBlank()) {
            return Optional.empty();
        }
        return Optional.of(annotation.value().trim());
    }

    private static List<PropertyNameProvider> providers() {
        List<PropertyNameProvider> providers = new ArrayList<>();
        ServiceLoader.load(PropertyNameProvider.class).forEach(providers::add);
        providers.sort(Comparator.comparingInt(PropertyNameProvider::order).reversed());
        return providers;
    }
}
