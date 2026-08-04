package io.github.thixvinix.commons.validation;

import io.github.thixvinix.commons.i18n.Messages;
import io.github.thixvinix.commons.i18n.MissingMessagePolicy;
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
import static org.mockito.Mockito.verify;

@ExtendWith(CommonsI18nExtension.class)
@DisplayName("AbstractValidator Tests")
class AbstractValidatorTest {

    private final ProbeValidator validator = new ProbeValidator();

    @BeforeEach
    void stubResolver() {
        Messages.setResolver((key, locale, args) -> switch (key) {
            case "test.greeting" -> Optional.of("Hello " + args[0] + "!");
            case "test.plain" -> Optional.of("plain message");
            case "test.payload" -> Optional.of("file " + args[0] + " is invalid");
            default -> Optional.empty();
        });
    }

    @AfterEach
    void resetMessages() {
        Messages.reset();
    }

    @Test
    @DisplayName("addConstraintViolation resolves the message, disables the default violation and registers the template")
    void addConstraintViolationRegistersResolvedMessage() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        validator.violation(captured.context(), "test.greeting", "Maria");

        verify(captured.context()).disableDefaultConstraintViolation();
        assertThat(captured.last()).isEqualTo("Hello Maria!");
    }

    @Test
    @DisplayName("addConstraintViolation resolves a message with no arguments")
    void addConstraintViolationWithoutArgs() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        validator.violation(captured.context(), "test.plain");

        assertThat(captured.last()).isEqualTo("plain message");
    }

    @Test
    @DisplayName("addConstraintViolationWithPropertyNode attaches the property node before adding the violation")
    void addConstraintViolationWithPropertyNode() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        validator.violationOnProperty(captured.context(), "someField", "test.plain");

        assertThat(captured.last()).isEqualTo("plain message");
    }

    @Test
    @DisplayName("A missing key falls back to the key itself under RETURN_KEY, and a violation is still registered")
    void missingKeyStillRegistersAViolation() {
        Messages.setMissingMessagePolicy(MissingMessagePolicy.RETURN_KEY);
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        validator.violation(captured.context(), "no.such.key");

        assertThat(captured.last()).isEqualTo("no.such.key");
    }

    @Test
    @DisplayName("An adversarial argument is escaped so it cannot be re-interpreted as template syntax")
    void adversarialArgumentIsEscaped() {
        ConstraintViolationCapture.Captured captured = ConstraintViolationCapture.mockContext();

        validator.violation(captured.context(), "test.payload", "a{0}.pdf");

        assertThat(captured.last()).isEqualTo("file a{0}.pdf is invalid");
    }

    @Test
    @DisplayName("validateMaxTotalSizeMB falls back to the default for non-positive values")
    void normalizesInvalidMaxTotalSize() {
        assertThat(validator.normalizeMaxTotalSizeMB(0)).isEqualTo(10);
        assertThat(validator.normalizeMaxTotalSizeMB(-5)).isEqualTo(10);
        assertThat(validator.normalizeMaxTotalSizeMB(7)).isEqualTo(7);
    }

    @Test
    @DisplayName("isNullOrEmpty(String) and isNullOrEmpty(Iterable) behave as expected")
    void nullOrEmptyChecks() {
        assertThat(validator.stringNullOrEmpty(null)).isTrue();
        assertThat(validator.stringNullOrEmpty("")).isTrue();
        assertThat(validator.stringNullOrEmpty("x")).isFalse();

        assertThat(validator.iterableNullOrEmpty(null)).isTrue();
        assertThat(validator.iterableNullOrEmpty(List.of())).isTrue();
        assertThat(validator.iterableNullOrEmpty(List.of("x"))).isFalse();
    }
}
