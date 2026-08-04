package io.github.thixvinix.commons.validation;

import io.github.thixvinix.commons.i18n.Messages;
import io.github.thixvinix.commons.i18n.testing.CommonsI18nExtension;
import io.github.thixvinix.commons.validation.testing.ConstraintViolationCapture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(CommonsI18nExtension.class)
@DisplayName("AbstractMapValidator Tests")
class AbstractMapValidatorTest {

    private final ProbeMapValidator validator = new ProbeMapValidator();

    @BeforeEach
    void stubResolver() {
        Messages.setResolver((key, locale, args) -> Optional.of(key + (args.length > 0 ? ":" + args[0] : "")));
    }

    @AfterEach
    void resetMessages() {
        Messages.reset();
    }

    @Test
    @DisplayName("validateMaxSize returns true and reports a violation when the map exceeds the max size")
    void detectsMapExceedingMaxSize() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        boolean exceeded = validator.maxSize(Map.of("a", 1, "b", 2, "c", 3), 2, captured.context(), "max.size");

        assertThat(exceeded).isTrue();
        assertThat(captured.last()).isEqualTo("max.size:2");
    }

    @Test
    @DisplayName("validateMaxSize returns false when the map is within the limit")
    void acceptsMapWithinMaxSize() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        assertThat(validator.maxSize(Map.of("a", 1), 2, captured.context(), "max.size")).isFalse();
    }

    @Test
    @DisplayName("validateTotalSize reports the accumulated size in MB when the limit is exceeded")
    void detectsTotalSizeExceeded() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();
        long oneMb = 1024L * 1024L;

        boolean exceeded = validator.totalSize(
                Map.of("a", 4L, "b", 4L), 5, size -> size * oneMb, captured.context(), "total.size");

        assertThat(exceeded).isTrue();
        assertThat(captured.last()).isEqualTo("total.size:8.0000");
    }

    @Test
    @DisplayName("validateTotalSize returns false when total size is within the limit")
    void acceptsTotalSizeWithinLimit() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();
        long oneMb = 1024L * 1024L;

        boolean exceeded = validator.totalSize(
                Map.of("a", 1L), 5, size -> size * oneMb, captured.context(), "total.size");

        assertThat(exceeded).isFalse();
    }
}
