package io.github.thixvinix.commons.validation.testing;

import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Stubs a mocked {@link ConstraintValidatorContext} so every
 * {@code buildConstraintViolationWithTemplate(...)} call — whether followed by a plain
 * {@code addConstraintViolation()} or by {@code addPropertyNode(...).addConstraintViolation()} —
 * is captured as a plain string, already un-escaped from the {@code \{ \} \$ \\} escaping applied
 * by {@code AbstractValidator}. This lets migrated tests keep asserting the same human-readable
 * expected strings used before the escaping was introduced.
 */
public final class ConstraintViolationCapture {

    private ConstraintViolationCapture() {
    }

    /**
     * Builds a mocked context and a list that fills up, in call order, with every captured message.
     */
    public static Captured mockContext() {
        List<String> messages = new ArrayList<>();

        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext.class);

        when(context.buildConstraintViolationWithTemplate(anyString())).thenAnswer(invocation -> {
            messages.add(unescape(invocation.getArgument(0)));
            return builder;
        });
        when(builder.addPropertyNode(anyString())).thenReturn(nodeBuilder);
        when(nodeBuilder.addConstraintViolation()).thenReturn(context);
        when(builder.addConstraintViolation()).thenReturn(context);

        return new Captured(context, messages);
    }

    /**
     * Reverses {@code ConstraintMessages.escapeTemplate(...)}: drops every backslash that precedes
     * a {@code \}, {@code {}, {@code }} or {@code $}.
     */
    public static String unescape(String escaped) {
        StringBuilder result = new StringBuilder(escaped.length());
        boolean escaping = false;
        for (int i = 0; i < escaped.length(); i++) {
            char c = escaped.charAt(i);
            if (!escaping && c == '\\') {
                escaping = true;
                continue;
            }
            escaping = false;
            result.append(c);
        }
        return result.toString();
    }

    public record Captured(ConstraintValidatorContext context, List<String> messages) {

        public String last() {
            return messages.isEmpty() ? null : messages.get(messages.size() - 1);
        }
    }
}
