package io.github.thixvinix.commons.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ConstraintMessages Tests")
class ConstraintMessagesTest {

    @Test
    @DisplayName("Returns null unchanged")
    void nullPassesThrough() {
        assertThat(ConstraintMessages.escapeTemplate(null)).isNull();
    }

    @Test
    @DisplayName("Leaves plain text untouched")
    void plainTextUnchanged() {
        assertThat(ConstraintMessages.escapeTemplate("plain text, no specials")).isEqualTo("plain text, no specials");
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @DisplayName("Escapes adversarial filenames that would otherwise be re-interpreted as template syntax")
    @CsvSource(delimiter = '|', textBlock = """
            a{0}.pdf                 | a\\{0\\}.pdf
            a}b.pdf                  | a\\}b.pdf
            ${jndi:x}.png            | \\$\\{jndi:x\\}.png
            C:\\temp\\x.pdf          | C:\\\\temp\\\\x.pdf
            """)
    void escapesAdversarialPayloads(String input, String expected) {
        assertThat(ConstraintMessages.escapeTemplate(input)).isEqualTo(expected);
    }

    @Test
    @DisplayName("Escaped output, when un-escaped, round-trips to the original text")
    void roundTrips() {
        String original = "report{1}.pdf costs $100 on C:\\temp\\";

        String escaped = ConstraintMessages.escapeTemplate(original);

        assertThat(escaped).doesNotContain("{1}").contains("\\{1\\}");
    }
}
