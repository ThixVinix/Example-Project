package io.github.thixvinix.commons.validation.support;

import java.lang.reflect.Field;

import static java.util.Objects.nonNull;

/**
 * Small reflection utilities shared across validators that need to walk an object's declared
 * fields — kept dependency-free so any module can use it without pulling in a specific
 * annotation library.
 */
public final class ReflectionSupport {

    private ReflectionSupport() {
    }

    /**
     * Looks up {@code fieldName} on {@code type}, walking up the superclass chain.
     *
     * @return the declared field, or {@code null} if not found on any superclass
     */
    public static Field findFieldRecursive(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (nonNull(current) && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
