package io.github.thixvinix.commons.dates;

import io.github.thixvinix.commons.validation.support.PropertyNameProvider;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * Resolves Spring's {@code @BindParam} name — on the field itself, or on a matching constructor
 * parameter — purely via reflection, so this module never needs a compile-time dependency on
 * spring-web. The annotation class is looked up by name once; if spring-web is not on the
 * classpath, this provider silently contributes nothing.
 * <p>
 * Reading the constructor parameter's {@code @BindParam} value requires the consumer's classes to
 * be compiled with {@code -parameters} (the Spring Boot Gradle/Maven plugin does this
 * automatically); otherwise parameter names fall back to synthetic {@code arg0, arg1, ...} and
 * this provider will not find a match.
 */
public final class ReflectivePropertyNameProvider implements PropertyNameProvider {

    private static final String BIND_PARAM_CLASS_NAME = "org.springframework.web.bind.annotation.BindParam";

    private static final Optional<Class<? extends Annotation>> BIND_PARAM_CLASS = loadBindParamClass();

    @Override
    public Optional<String> nameFor(Class<?> owner, Field field) {
        if (BIND_PARAM_CLASS.isEmpty()) {
            return Optional.empty();
        }

        Optional<String> onField = bindParamValue(field.getAnnotation(BIND_PARAM_CLASS.get()));
        if (onField.isPresent()) {
            return onField;
        }

        return constructorBindParamValue(owner, field.getName());
    }

    private Optional<String> constructorBindParamValue(Class<?> owner, String fieldName) {
        for (Constructor<?> constructor : owner.getDeclaredConstructors()) {
            for (Parameter parameter : constructor.getParameters()) {
                if (!parameter.getName().equals(fieldName)) {
                    continue;
                }
                Optional<String> value = bindParamValue(parameter.getAnnotation(BIND_PARAM_CLASS.get()));
                if (value.isPresent()) {
                    return value;
                }
            }
        }
        return Optional.empty();
    }

    private Optional<String> bindParamValue(Annotation annotation) {
        if (annotation == null) {
            return Optional.empty();
        }
        try {
            Method valueMethod = annotation.annotationType().getMethod("value");
            Object value = valueMethod.invoke(annotation);
            if (value instanceof String stringValue && !stringValue.isBlank()) {
                return Optional.of(stringValue.trim());
            }
        } catch (ReflectiveOperationException ignored) {
            // BindParam not shaped as expected; treat as absent.
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private static Optional<Class<? extends Annotation>> loadBindParamClass() {
        try {
            return Optional.of((Class<? extends Annotation>) Class.forName(
                    BIND_PARAM_CLASS_NAME, false, ReflectivePropertyNameProvider.class.getClassLoader()));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
