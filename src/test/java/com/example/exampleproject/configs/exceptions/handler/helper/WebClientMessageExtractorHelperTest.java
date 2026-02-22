package com.example.exampleproject.configs.exceptions.handler.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Method test for {@link WebClientMessageExtractorHelper}
 */
@Tag(value = "WebClientMessageExtractorHelper_Tests")
@DisplayName("WebClientMessageExtractorHelper Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class WebClientMessageExtractorHelperTest {

    private static final String EXTRACT_MESSAGE = "extractMessageFromWebClientResponseException";

    /**
     * Method test for
     * {@link WebClientMessageExtractorHelper#extractMessageFromWebClientResponseException(WebClientResponseException)}
     */
    @Order(1)
    @Tag(value = EXTRACT_MESSAGE)
    @DisplayName(EXTRACT_MESSAGE + " Given JSON with 'message' field, then should extract it")
    @Test
    void extractMessage_WhenJsonHasMessageField_ThenShouldExtractIt() {
        // Arrange
        String json = "{\"message\": \"Detailed error message\"}";
        WebClientResponseException ex = WebClientResponseException.create(
                400, "Bad Request", null, json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        // Act
        Optional<String> result = WebClientMessageExtractorHelper.extractMessageFromWebClientResponseException(ex);

        // Assert
        assertTrue(result.isPresent(), "Message should be extracted");
        assertEquals("Detailed error message", result.get(), "The extracted message should match");
    }

    /**
     * Method test for
     * {@link WebClientMessageExtractorHelper#extractMessageFromWebClientResponseException(WebClientResponseException)}
     */
    @Order(2)
    @Tag(value = EXTRACT_MESSAGE)
    @DisplayName(EXTRACT_MESSAGE + " Given JSON with 'error' field, then should extract it")
    @Test
    void extractMessage_WhenJsonHasErrorField_ThenShouldExtractIt() {
        // Arrange
        String json = "{\"error\": \"Error description\"}";
        WebClientResponseException ex = WebClientResponseException.create(
                400, "Bad Request", null, json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        // Act
        Optional<String> result = WebClientMessageExtractorHelper.extractMessageFromWebClientResponseException(ex);

        // Assert
        assertTrue(result.isPresent(), "Message should be extracted from 'error' field");
        assertEquals("Error description", result.get(), "The extracted message should match");
    }

    /**
     * Method test for
     * {@link WebClientMessageExtractorHelper#extractMessageFromWebClientResponseException(WebClientResponseException)}
     */
    @Order(3)
    @Tag(value = EXTRACT_MESSAGE)
    @DisplayName(EXTRACT_MESSAGE + " Given JSON with nested 'message', then should extract it")
    @Test
    void extractMessage_WhenJsonHasNestedMessage_ThenShouldExtractIt() {
        // Arrange
        String json = "{\"errors\": {\"message\": \"Nested message\"}}";
        WebClientResponseException ex = WebClientResponseException.create(
                400, "Bad Request", null, json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        // Act
        Optional<String> result = WebClientMessageExtractorHelper.extractMessageFromWebClientResponseException(ex);

        // Assert
        assertTrue(result.isPresent(), "Nested message should be extracted");
        assertEquals("Nested message", result.get(), "The extracted message should match");
    }

    /**
     * Method test for
     * {@link WebClientMessageExtractorHelper#extractMessageFromWebClientResponseException(WebClientResponseException)}
     */
    @Order(4)
    @Tag(value = EXTRACT_MESSAGE)
    @DisplayName(EXTRACT_MESSAGE + " Given empty response body, then should return empty optional")
    @Test
    void extractMessage_WhenEmptyBody_ThenShouldReturnEmptyOptional() {
        // Arrange
        WebClientResponseException ex = WebClientResponseException.create(
                400, "Bad Request", null, new byte[0], StandardCharsets.UTF_8);

        // Act
        Optional<String> result = WebClientMessageExtractorHelper.extractMessageFromWebClientResponseException(ex);

        // Assert
        assertTrue(result.isEmpty(), "Result should be empty for empty body");
    }

    /**
     * Method test for
     * {@link WebClientMessageExtractorHelper#extractMessageFromWebClientResponseException(WebClientResponseException)}
     */
    @Order(5)
    @Tag(value = EXTRACT_MESSAGE)
    @DisplayName(EXTRACT_MESSAGE + " Given malformed JSON, then should return empty optional")
    @Test
    void extractMessage_WhenMalformedJson_ThenShouldReturnEmptyOptional() {
        // Arrange
        String json = "{malformed: json}";
        WebClientResponseException ex = WebClientResponseException.create(
                400, "Bad Request", null, json.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        // Act
        Optional<String> result = WebClientMessageExtractorHelper.extractMessageFromWebClientResponseException(ex);

        // Assert
        assertTrue(result.isEmpty(), "Result should be empty for malformed JSON");
    }
}
