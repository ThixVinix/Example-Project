package io.github.thixvinix.commons.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Guards the "core modules never depend on Spring" rule over time: if anyone adds a Spring
 * dependency to this module (directly or transitively), this test starts failing instead of the
 * violation only surfacing much later as a surprise for a non-Spring consumer.
 */
@DisplayName("No Spring On Classpath Tests")
class NoSpringOnClasspathTest {

    @Test
    @DisplayName("org.springframework.context.MessageSource is not on the test runtime classpath")
    void springMessageSourceIsAbsent() {
        assertThatThrownBy(() -> Class.forName("org.springframework.context.MessageSource"))
                .isInstanceOf(ClassNotFoundException.class);
    }
}
