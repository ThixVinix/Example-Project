package io.github.thixvinix.commons.validation;

/**
 * {@code buildConstraintViolationWithTemplate(String)} re-parses the string it receives as a
 * Bean Validation message template. Since every validator in this library resolves its message
 * up front (via {@code io.github.thixvinix.commons.i18n.Messages}) and only then hands the
 * already-resolved text to that method, any {@code {}} or {@code $} coming from an interpolated
 * argument (e.g. a user-supplied filename) would otherwise be re-interpreted as a template
 * token — breaking the message or, with an EL implementation on the classpath, evaluating it.
 * {@link #escapeTemplate(String)} neutralizes that by escaping the four characters Hibernate
 * Validator's interpolator treats specially, which is the officially supported way to embed
 * literal text in a constraint message template.
 */
public final class ConstraintMessages {

    private ConstraintMessages() {
    }

    public static String escapeTemplate(String resolved) {
        if (resolved == null) {
            return null;
        }

        StringBuilder escaped = new StringBuilder(resolved.length());
        for (int i = 0; i < resolved.length(); i++) {
            char c = resolved.charAt(i);
            if (c == '\\' || c == '{' || c == '}' || c == '$') {
                escaped.append('\\');
            }
            escaped.append(c);
        }
        return escaped.toString();
    }
}
