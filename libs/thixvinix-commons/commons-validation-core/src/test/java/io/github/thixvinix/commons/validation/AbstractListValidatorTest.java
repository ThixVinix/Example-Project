package io.github.thixvinix.commons.validation;

import io.github.thixvinix.commons.i18n.Messages;
import io.github.thixvinix.commons.i18n.testing.CommonsI18nExtension;
import io.github.thixvinix.commons.validation.testing.ConstraintViolationCapture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(CommonsI18nExtension.class)
@DisplayName("AbstractListValidator Tests")
class AbstractListValidatorTest {

    private final ProbeListValidator validator = new ProbeListValidator();

    @BeforeEach
    void stubResolver() {
        Messages.setResolver((key, locale, args) -> Optional.of(key + (args.length > 0 ? ":" + args[0] : "")));
    }

    @AfterEach
    void resetMessages() {
        Messages.reset();
    }

    @Test
    @DisplayName("validateMaxSize returns true and reports a violation when the list exceeds the max size")
    void detectsListExceedingMaxSize() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        boolean exceeded = validator.maxSize(List.of("a", "b", "c"), 2, captured.context(), "max.size");

        assertThat(exceeded).isTrue();
        assertThat(captured.last()).isEqualTo("max.size:2");
    }

    @Test
    @DisplayName("validateMaxSize returns false when the list is within the limit")
    void acceptsListWithinMaxSize() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        assertThat(validator.maxSize(List.of("a"), 2, captured.context(), "max.size")).isFalse();
    }

    @Test
    @DisplayName("hasDuplicateItems detects the first repeated element using the caller-supplied message key")
    void detectsDuplicates() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        boolean hasDuplicates = validator.duplicates(List.of("a", "b", "a"), captured.context(), "dup.key");

        assertThat(hasDuplicates).isTrue();
        assertThat(captured.last()).isEqualTo("dup.key");
    }

    @Test
    @DisplayName("hasDuplicateItems ignores null elements")
    void nullsAreNotCountedAsDuplicates() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();
        List<String> withNulls = new java.util.ArrayList<>();
        withNulls.add(null);
        withNulls.add(null);

        assertThat(validator.duplicates(withNulls, captured.context(), "dup.key")).isFalse();
    }

    @Test
    @DisplayName("hasInvalidItem reports the 1-based index of the first invalid element")
    void reportsFirstInvalidItemIndex() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        boolean invalid = validator.invalidItem(List.of("ok", "bad"),
                (item, ctx) -> !item.equals("bad"), captured.context(), "invalid.item");

        assertThat(invalid).isTrue();
        assertThat(captured.last()).isEqualTo("invalid.item:2");
    }

    @Test
    @DisplayName("validateTotalSize reports the accumulated size in MB when the limit is exceeded")
    void detectsTotalSizeExceeded() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();
        long oneMb = 1024L * 1024L;

        boolean exceeded = validator.totalSize(List.of(6L, 6L), 10, size -> size * oneMb, captured.context(), "total.size");

        assertThat(exceeded).isTrue();
        assertThat(captured.last()).isEqualTo("total.size:12.0000");
    }

    @Test
    @DisplayName("validateTotalSize normalizes a non-positive limit to the default before comparing")
    void normalizesInvalidLimitBeforeComparing() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();
        long oneMb = 1024L * 1024L;

        boolean exceeded = validator.totalSize(List.of(5L), 0, size -> size * oneMb, captured.context(), "total.size");

        assertThat(exceeded).isFalse();
    }
}
