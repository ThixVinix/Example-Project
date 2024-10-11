package com.example.exampleproject.configs.exceptions.handler;


import com.example.exampleproject.configs.exceptions.ErrorResponse;
import com.example.exampleproject.configs.exceptions.handler.helper.ExceptionHandlerMessageHelper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link GlobalExceptionHandler}
 */
@SpringBootTest
@AutoConfigureMockMvc
@Tag(value = "GlobalExceptionHandler_Tests")
@DisplayName("GlobalExceptionHandler Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GlobalExceptionHandlerTest {

    private static final String HANDLE_RESOURCE_NOT_FOUND_EXCEPTION = "handleResourceNotFoundException";

    private static final String HANDLE_METHOD_NOT_SUPPORTED_EXCEPTION = "handleHttpRequestMethodNotSupportedException";

    private static final String HANDLE_BAD_REQUEST_EXCEPTION = "handleBadRequestException";

    private static final String GET_INTERNAL_SERVER_ERROR_MESSAGE = "handleGlobalException";

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleResourceNotFoundException(Exception, WebRequest)}
     */
    @Order(1)
    @Tag(value = HANDLE_RESOURCE_NOT_FOUND_EXCEPTION)
    @DisplayName(HANDLE_RESOURCE_NOT_FOUND_EXCEPTION +
            " - When ResourceNotFoundException is thrown then return not found status")
    @Test
    void testHandleResourceNotFoundException() {

        Exception ex = new Exception("Resource not found");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("error", "Custom not found message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getNotFoundMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity =
                    exceptionHandler.handleResourceNotFoundException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.status());
            assertEquals("Custom not found message", errorResponse.messages().get("error"));
            assertEquals(HttpStatus.NOT_FOUND.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }


    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleHttpRequestMethodNotSupportedException(
     *HttpRequestMethodNotSupportedException, WebRequest)}
     */
    @Order(2)
    @Tag(value = HANDLE_METHOD_NOT_SUPPORTED_EXCEPTION)
    @DisplayName(HANDLE_METHOD_NOT_SUPPORTED_EXCEPTION +
            " - When HttpRequestMethodNotSupportedException is thrown then return method not allowed status")
    @Test
    void testHandleHttpRequestMethodNotSupportedException() {

        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("message", "Custom method not allowed message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() ->
                    ExceptionHandlerMessageHelper.getMethodNotAllowedMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity =
                    exceptionHandler.handleHttpRequestMethodNotSupportedException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());

            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), errorResponse.status());
            assertEquals("Custom method not allowed message", errorResponse.messages().get("message"));
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleBadRequestException(Exception, WebRequest)}
     */
    @Order(3)
    @Tag(value = HANDLE_BAD_REQUEST_EXCEPTION)
    @DisplayName(HANDLE_BAD_REQUEST_EXCEPTION +
            " - When BadRequest Exception is thrown then return bad request status")
    @Test
    void testHandleBadRequestException() {

        Exception ex = new Exception("Bad request");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("fieldOne", "Custom bad request message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getBadRequestMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity =
                    exceptionHandler.handleBadRequestException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
            assertEquals("Custom bad request message", errorResponse.messages().get("fieldOne"));
            assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleGlobalException(Exception, WebRequest)}
     */
    @Order(4)
    @Tag(value = GET_INTERNAL_SERVER_ERROR_MESSAGE)
    @DisplayName(GET_INTERNAL_SERVER_ERROR_MESSAGE +
            " - When Exception is thrown then return internal server error status")
    @Test
    void testHandleGlobalException() {

        Exception ex = new Exception("Unexpected error");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("error", "Custom internal server error message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() ->
                    ExceptionHandlerMessageHelper.getInternalServerErrorMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity =
                    exceptionHandler.handleGlobalException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.status());
            assertEquals("Custom internal server error message", errorResponse.messages().get("error"));
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }


}
