package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.BaseError;
import com.example.exampleproject.configs.exceptions.ErrorMultipleResponse;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Method test for {@link ErrorResponseFactory}
 */
@Tag(value = "ErrorResponseFactory_Tests")
@DisplayName("ErrorResponseFactory Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class ErrorResponseFactoryTest {

    private static final String CREATE_APPROPRIATE_ERROR_RESPONSE = "createAppropriateErrorResponse";
    private static final String CREATE_ERROR_SINGLE_RESPONSE = "createErrorSingleResponse";
    private static final String CREATE_ERROR_MULTIPLE_RESPONSE = "createErrorMultipleResponse";
    private static final String TEST_PATH = "/test/path";
    private static final String ERROR_MESSAGE = "Error message";

    /**
     * Method test for {@link ErrorResponseFactory#createAppropriateErrorResponse(Map, String)}
     */
    @Order(1)
    @Tag(value = CREATE_APPROPRIATE_ERROR_RESPONSE)
    @DisplayName(CREATE_APPROPRIATE_ERROR_RESPONSE + " Given single message, then should return ErrorSingleResponse")
    @Test
    void createAppropriateErrorResponse_WhenSingleMessage_ThenShouldReturnErrorSingleResponse() {
        // Arrange
        Map<String, String> messages = new HashMap<>();
        messages.put("message", ERROR_MESSAGE);

        // Act
        BaseError result = ErrorResponseFactory.createAppropriateErrorResponse(messages, TEST_PATH);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertInstanceOf(ErrorSingleResponse.class, result, "The result should be an instance of ErrorSingleResponse");
        ErrorSingleResponse singleResponse = (ErrorSingleResponse) result;
        assertEquals(HttpStatus.BAD_REQUEST.value(), singleResponse.status(), "The status should be 400");
        assertEquals(ERROR_MESSAGE, singleResponse.message(), "The message should match");
        assertEquals(TEST_PATH, singleResponse.path(), "The path should match");
    }

    /**
     * Method test for {@link ErrorResponseFactory#createAppropriateErrorResponse(Map, String)}
     */
    @Order(2)
    @Tag(value = CREATE_APPROPRIATE_ERROR_RESPONSE)
    @DisplayName(CREATE_APPROPRIATE_ERROR_RESPONSE + " Given multiple messages, then should return ErrorMultipleResponse")
    @Test
    void createAppropriateErrorResponse_WhenMultipleMessages_ThenShouldReturnErrorMultipleResponse() {
        // Arrange
        Map<String, String> messages = new HashMap<>();
        messages.put("field1", "error1");
        messages.put("field2", "error2");

        // Act
        BaseError result = ErrorResponseFactory.createAppropriateErrorResponse(messages, TEST_PATH);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertInstanceOf(ErrorMultipleResponse.class, result, "The result should be an instance of ErrorMultipleResponse");
        ErrorMultipleResponse multipleResponse = (ErrorMultipleResponse) result;
        assertEquals(HttpStatus.BAD_REQUEST.value(), multipleResponse.status(), "The status should be 400");
        assertEquals(2, multipleResponse.messages().size(), "The messages map size should be 2");
        assertEquals(TEST_PATH, multipleResponse.path(), "The path should match");
    }

    /**
     * Method test for {@link ErrorResponseFactory#createErrorSingleResponse(HttpStatus, String, String)}
     */
    @Order(3)
    @Tag(value = CREATE_ERROR_SINGLE_RESPONSE)
    @DisplayName(CREATE_ERROR_SINGLE_RESPONSE + " Given parameters, then should create correct response")
    @Test
    void createErrorSingleResponse_WhenParametersProvided_ThenShouldCreateCorrectResponse() {
        // Arrange
        HttpStatus status = HttpStatus.NOT_FOUND;

        // Act
        ErrorSingleResponse result = ErrorResponseFactory.createErrorSingleResponse(status, ERROR_MESSAGE, TEST_PATH);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(status.value(), result.status(), "The status value should match");
        assertEquals(status.getReasonPhrase(), result.error(), "The error reason phrase should match");
        assertEquals(ERROR_MESSAGE, result.message(), "The message should match");
        assertEquals(TEST_PATH, result.path(), "The path should match");
        assertNotNull(result.timestamp(), "The timestamp should not be null");
    }

    /**
     * Method test for {@link ErrorResponseFactory#createErrorMultipleResponse(Map, String)}
     */
    @Order(4)
    @Tag(value = CREATE_ERROR_MULTIPLE_RESPONSE)
    @DisplayName(CREATE_ERROR_MULTIPLE_RESPONSE + " Given parameters, then should create correct response")
    @Test
    void createErrorMultipleResponse_WhenParametersProvided_ThenShouldCreateCorrectResponse() {
        // Arrange
        Map<String, String> messages = Map.of("field", "error");

        // Act
        ErrorMultipleResponse result = ErrorResponseFactory.createErrorMultipleResponse(messages, TEST_PATH);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.status(), "The status should be 400");
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), result.error(), "The error reason phrase should match");
        assertEquals(messages, result.messages(), "The messages should match");
        assertEquals(TEST_PATH, result.path(), "The path should match");
        assertNotNull(result.timestamp(), "The timestamp should not be null");
    }
}
