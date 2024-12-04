package com.example.exampleproject.configs.exceptions.handler;


import com.example.exampleproject.configs.exceptions.ErrorResponse;
import com.example.exampleproject.configs.exceptions.custom.DataIntegrityViolationException;
import com.example.exampleproject.configs.exceptions.custom.UnauthorizedException;
import com.example.exampleproject.configs.exceptions.handler.helper.ExceptionHandlerMessageHelper;
import feign.FeignException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Stream;

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

    public static final String HANDLE_FEIGN_CLIENT_EXCEPTION = "handleFeignClientException";

    public static final String HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION =
            "handleHttpMediaTypeNotSupportedException";

    public static final String HANDLE_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION =
            "handleHttpMediaTypeNotAcceptableException";

    public static final String HANDLE_TIMEOUT_EXCEPTION = "handleTimeoutException";

    public static final String HANDLE_CONFLICT_EXCEPTION = "handleConflictException";

    public static final String HANDLE_FORBIDDEN_EXCEPTION = "handleForbiddenException";

    public static final String HANDLE_UNAUTHORIZED_EXCEPTION = "handleUnauthorizedException";

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
     * {@link GlobalExceptionHandler#handleHttpRequestMethodNotSupportedException(Exception, WebRequest)}
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

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleUnauthorizedException(Exception, WebRequest)}
     */
    @Order(5)
    @Tag(value = HANDLE_UNAUTHORIZED_EXCEPTION)
    @DisplayName(HANDLE_UNAUTHORIZED_EXCEPTION + " - When UnauthorizedException is thrown then return " +
            "unauthorized status")
    @Test
    void testHandleUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized access");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("error", "Custom unauthorized message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getUnauthorizedMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleUnauthorizedException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.UNAUTHORIZED.value(), errorResponse.status());
            assertEquals("Custom unauthorized message", errorResponse.messages().get("error"));
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleForbiddenException(Exception, WebRequest)}
     */
    @Order(6)
    @Tag(value = HANDLE_FORBIDDEN_EXCEPTION)
    @DisplayName(HANDLE_FORBIDDEN_EXCEPTION + "handleForbiddenException - When AccessDeniedException is thrown " +
            "then return forbidden status")
    @Test
    void testHandleForbiddenException() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("error", "Custom forbidden message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getForbiddenMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleForbiddenException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.FORBIDDEN.value(), errorResponse.status());
            assertEquals("Custom forbidden message", errorResponse.messages().get("error"));
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleForbiddenException(Exception, WebRequest)}
     */
    @Order(7)
    @Tag(value = HANDLE_CONFLICT_EXCEPTION)
    @DisplayName(HANDLE_CONFLICT_EXCEPTION + " - When DataIntegrityViolationException is thrown then return " +
            "conflict status")
    @Test
    void testHandleConflictException() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Data conflict");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("error", "Custom conflict message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getConflictMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleConflictException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.CONFLICT.value(), errorResponse.status());
            assertEquals("Custom conflict message", errorResponse.messages().get("error"));
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleTimeoutException(Exception, WebRequest)}
     */
    @Order(8)
    @Tag(value = HANDLE_TIMEOUT_EXCEPTION)
    @DisplayName(HANDLE_TIMEOUT_EXCEPTION + " - When TimeoutException is thrown then return timeout status")
    @Test
    void testHandleTimeoutException() {
        TimeoutException ex = new TimeoutException("Request timeout");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("error", "Custom timeout message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getTimeoutMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleTimeoutException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.REQUEST_TIMEOUT, responseEntity.getStatusCode());
            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.REQUEST_TIMEOUT.value(), errorResponse.status());
            assertEquals("Custom timeout message", errorResponse.messages().get("error"));
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleHttpMediaTypeNotAcceptableException(Exception, WebRequest)}
     */
    @Order(9)
    @Tag(value = HANDLE_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION)
    @DisplayName(HANDLE_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION + " - When HttpMediaTypeNotAcceptableException " +
            "is thrown then return not acceptable status")
    @Test
    void testHttpMediaTypeNotAcceptableException() {
        HttpMediaTypeNotAcceptableException ex =
                new HttpMediaTypeNotAcceptableException("Request HttpMediaTypeNotAcceptableException");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("error", "Custom not acceptable message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() ->
                    ExceptionHandlerMessageHelper.getHttpMediaTypeNotAcceptableException(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity =
                    exceptionHandler.handleHttpMediaTypeNotAcceptableException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), errorResponse.status());
            assertEquals("Custom not acceptable message", errorResponse.messages().get("error"));
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleHttpMediaTypeNotSupportedException(Exception, WebRequest)}
     */
    @Order(10)
    @Tag(value = HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION)
    @DisplayName(HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION + " - When HttpMediaTypeNotSupportedException " +
            "is thrown then return unsupported media type status")
    @Test
    void testHttpMediaTypeNotSupportedException() {
        HttpMediaTypeNotSupportedException ex =
                new HttpMediaTypeNotSupportedException("Request HttpMediaTypeNotSupportedException");
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("error", "Custom unsupported media type message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() ->
                    ExceptionHandlerMessageHelper.getHttpMediaTypeNotSupportedException(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity =
                    exceptionHandler.handleHttpMediaTypeNotSupportedException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, responseEntity.getStatusCode());
            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), errorResponse.status());
            assertEquals("Custom unsupported media type message", errorResponse.messages().get("error"));
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleFeignClientException(FeignException, WebRequest)}
     */
    @Order(11)
    @Tag(value = HANDLE_FEIGN_CLIENT_EXCEPTION)
    @DisplayName(HANDLE_FEIGN_CLIENT_EXCEPTION + " - When FeignException is thrown then handle accordingly")
    @ParameterizedTest
    @MethodSource("feignClientExceptionProvider")
    void testHandleFeignClientException(int status,
                                        HttpStatus expectedStatus,
                                        String expectedMessage,
                                        Function<FeignException, Map<String, String>> messageFunction) {

        FeignException ex = mock(FeignException.class);
        when(ex.status()).thenReturn(status);
        when(ex.getMessage()).thenReturn("Feign client error");

        feign.Request requestMock = mock(feign.Request.class);
        when(requestMock.headers()).thenReturn(new HashMap<>());
        when(ex.request()).thenReturn(requestMock);

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("error", expectedMessage);

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> messageFunction.apply(ex)).thenReturn(mockMessages);

            ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleFeignClientException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(expectedStatus, responseEntity.getStatusCode());
            ErrorResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(expectedStatus.value(), errorResponse.status());
            assertEquals(expectedMessage, errorResponse.messages().get("error"));
        }
    }

    static Stream<Arguments> feignClientExceptionProvider() {
        return Stream.of(
                Arguments.of(401, HttpStatus.UNAUTHORIZED, "Custom unauthorized message",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getUnauthorizedMessage),
                Arguments.of(400, HttpStatus.BAD_REQUEST, "Custom bad request message",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getBadRequestMessage),
                Arguments.of(404, HttpStatus.NOT_FOUND, "Custom not found message",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getNotFoundMessage),
                Arguments.of(405, HttpStatus.METHOD_NOT_ALLOWED, "Custom method not allowed message",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getMethodNotAllowedMessage),
                Arguments.of(403, HttpStatus.FORBIDDEN, "Custom forbidden message",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getForbiddenMessage),
                Arguments.of(408, HttpStatus.REQUEST_TIMEOUT, "Custom timeout message",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getTimeoutMessage),
                Arguments.of(409, HttpStatus.CONFLICT, "Custom conflict message",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getConflictMessage),
                Arguments.of(-1, HttpStatus.INTERNAL_SERVER_ERROR, "Custom internal server error message1",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getInternalServerErrorMessage),
                Arguments.of(500, HttpStatus.INTERNAL_SERVER_ERROR, "Custom internal server error message2",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getInternalServerErrorMessage),
                Arguments.of(406, HttpStatus.NOT_ACCEPTABLE, "Custom not acceptable message",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getHttpMediaTypeNotAcceptableException),
                Arguments.of(415, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Custom unsupported media type message",
                        (Function<FeignException, Map<String, String>>)
                                ExceptionHandlerMessageHelper::getHttpMediaTypeNotSupportedException)
        );
    }

}
