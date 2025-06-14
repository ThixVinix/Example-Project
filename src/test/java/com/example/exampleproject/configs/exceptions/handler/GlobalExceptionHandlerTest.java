package com.example.exampleproject.configs.exceptions.handler;


import com.example.exampleproject.configs.exceptions.BaseError;
import com.example.exampleproject.configs.exceptions.ErrorMultipleResponse;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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

    private static final String HANDLE_FEIGN_CLIENT_EXCEPTION = "handleFeignClientException";

    private static final String HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION =
            "handleHttpMediaTypeNotSupportedException";

    private static final String HANDLE_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION =
            "handleHttpMediaTypeNotAcceptableException";

    private static final String HANDLE_TIMEOUT_EXCEPTION = "handleTimeoutException";

    private static final String HANDLE_CONFLICT_EXCEPTION = "handleConflictException";

    private static final String HANDLE_FORBIDDEN_EXCEPTION = "handleForbiddenException";

    private static final String HANDLE_UNAUTHORIZED_EXCEPTION = "handleUnauthorizedException";

    private static final String HANDLE_MAX_UPLOAD_SIZE_EXCEEDED_EXCEPTION = "handleMaxUploadSizeExceededException";

    record HandlerConfig(Function<FeignException, ?> function, boolean returnsString) {
    }

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

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {

            mockedStatic
                    .when(() -> ExceptionHandlerMessageHelper.getNotFoundMessage(ex))
                    .thenReturn("Custom not found message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleResourceNotFoundException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

            ErrorSingleResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.status());
            assertEquals("Custom not found message", errorResponse.message());
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

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {

            mockedStatic.when(() ->
                            ExceptionHandlerMessageHelper.getMethodNotAllowedMessage(ex))
                    .thenReturn("Custom method not allowed message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleHttpRequestMethodNotSupportedException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());

            ErrorSingleResponse errorSingleResponse = responseEntity.getBody();
            assertNotNull(errorSingleResponse);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), errorSingleResponse.status());
            assertEquals("Custom method not allowed message", errorSingleResponse.message());
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), errorSingleResponse.error());
            assertEquals("/test/path", errorSingleResponse.path());
            assertNotNull(errorSingleResponse.timestamp());
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

            ResponseEntity<? extends BaseError> responseEntity =
                    exceptionHandler.handleBadRequestException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            BaseError responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorMultipleResponse.class, responseBody,
                    "Expected ErrorMultipleResponse but got " + responseBody.getClass().getSimpleName());

            ErrorMultipleResponse errorMultipleResponse = (ErrorMultipleResponse) responseBody;
            assertEquals(HttpStatus.BAD_REQUEST.value(), errorMultipleResponse.status());
            assertEquals("Custom bad request message", errorMultipleResponse.messages().get("fieldOne"));
            assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorMultipleResponse.error());
            assertEquals("/test/path", errorMultipleResponse.path());
            assertNotNull(errorMultipleResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleBadRequestException(Exception, WebRequest)}
     * when the message map contains only the "message" key
     */
    @Order(4)
    @Tag(value = HANDLE_BAD_REQUEST_EXCEPTION)
    @DisplayName(HANDLE_BAD_REQUEST_EXCEPTION +
            " - When BadRequest Exception is thrown with only 'message' key then return single error response")
    @Test
    void testHandleBadRequestExceptionWithSingleMessage() {
        Exception ex = new Exception("Bad request with single message");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("message", "Custom single error message");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getBadRequestMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<? extends BaseError> responseEntity =
                    exceptionHandler.handleBadRequestException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            BaseError responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorSingleResponse.class, responseBody,
                    "Expected ErrorSingleResponse but got " + responseBody.getClass().getSimpleName());

            ErrorSingleResponse errorSingleResponse = (ErrorSingleResponse) responseBody;
            assertEquals(HttpStatus.BAD_REQUEST.value(), errorSingleResponse.status());
            assertEquals("Custom single error message", errorSingleResponse.message());
            assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorSingleResponse.error());
            assertEquals("/test/path", errorSingleResponse.path());
            assertNotNull(errorSingleResponse.timestamp());
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

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() ->
                            ExceptionHandlerMessageHelper.getInternalServerErrorMessage(ex))
                    .thenReturn("Custom internal server error message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleGlobalException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

            ErrorSingleResponse errorSingleResponse = responseEntity.getBody();
            assertNotNull(errorSingleResponse);
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorSingleResponse.status());
            assertEquals("Custom internal server error message", errorSingleResponse.message());
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), errorSingleResponse.error());
            assertEquals("/test/path", errorSingleResponse.path());
            assertNotNull(errorSingleResponse.timestamp());
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

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getUnauthorizedMessage(ex))
                    .thenReturn("Custom unauthorized message");

            ResponseEntity<ErrorSingleResponse> responseEntity = exceptionHandler.handleUnauthorizedException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
            ErrorSingleResponse errorSingleResponse = responseEntity.getBody();
            assertNotNull(errorSingleResponse);
            assertEquals(HttpStatus.UNAUTHORIZED.value(), errorSingleResponse.status());
            assertEquals("Custom unauthorized message", errorSingleResponse.message());
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

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getForbiddenMessage(ex))
                    .thenReturn("Custom forbidden message");

            ResponseEntity<ErrorSingleResponse> responseEntity = exceptionHandler.handleForbiddenException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
            ErrorSingleResponse errorSingleResponse = responseEntity.getBody();
            assertNotNull(errorSingleResponse);
            assertEquals(HttpStatus.FORBIDDEN.value(), errorSingleResponse.status());
            assertEquals("Custom forbidden message", errorSingleResponse.message());
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

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getConflictMessage(ex))
                    .thenReturn("Custom conflict message");

            ResponseEntity<ErrorSingleResponse> responseEntity = exceptionHandler.handleConflictException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
            ErrorSingleResponse errorSingleResponse = responseEntity.getBody();
            assertNotNull(errorSingleResponse);
            assertEquals(HttpStatus.CONFLICT.value(), errorSingleResponse.status());
            assertEquals("Custom conflict message", errorSingleResponse.message());
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

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getTimeoutMessage(ex))
                    .thenReturn("Custom timeout message");

            ResponseEntity<ErrorSingleResponse> responseEntity = exceptionHandler.handleTimeoutException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.REQUEST_TIMEOUT, responseEntity.getStatusCode());
            ErrorSingleResponse errorSingleResponse = responseEntity.getBody();
            assertNotNull(errorSingleResponse);
            assertEquals(HttpStatus.REQUEST_TIMEOUT.value(), errorSingleResponse.status());
            assertEquals("Custom timeout message", errorSingleResponse.message());
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

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() ->
                            ExceptionHandlerMessageHelper.getHttpMediaTypeNotAcceptableException(ex))
                    .thenReturn("Custom not acceptable message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleHttpMediaTypeNotAcceptableException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
            ErrorSingleResponse errorSingleResponse = responseEntity.getBody();
            assertNotNull(errorSingleResponse);
            assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), errorSingleResponse.status());
            assertEquals("Custom not acceptable message", errorSingleResponse.message());
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

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() ->
                            ExceptionHandlerMessageHelper.getHttpMediaTypeNotSupportedException(ex))
                    .thenReturn("Custom unsupported media type message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleHttpMediaTypeNotSupportedException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, responseEntity.getStatusCode());
            ErrorSingleResponse errorSingleResponse = responseEntity.getBody();
            assertNotNull(errorSingleResponse);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), errorSingleResponse.status());
            assertEquals("Custom unsupported media type message", errorSingleResponse.message());
        }
    }


    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleMaxUploadSizeExceededException(Exception, WebRequest)}
     */
    @Order(11)
    @Tag(value = HANDLE_MAX_UPLOAD_SIZE_EXCEEDED_EXCEPTION)
    @DisplayName(HANDLE_MAX_UPLOAD_SIZE_EXCEEDED_EXCEPTION + " - When MaxUploadSizeExceededException is thrown then " +
            "return Payload Too Large status")
    @Test
    void testHandleMaxUploadSizeExceededException() {

        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(5000000); // limite de 5MB simulado
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/upload-path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {

            mockedStatic.when(() ->
                            ExceptionHandlerMessageHelper.getMaxUploadSizeExceededException(ex))
                    .thenReturn("The uploaded file size exceeds the allowed limit of 5MB.");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleMaxUploadSizeExceededException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, responseEntity.getStatusCode());

            ErrorSingleResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE.value(), errorResponse.status());
            assertEquals("The uploaded file size exceeds the allowed limit of 5MB.", errorResponse.message());
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/upload-path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleFeignClientException(FeignException, WebRequest)}
     */
    @Order(12)
    @Tag(value = HANDLE_FEIGN_CLIENT_EXCEPTION)
    @DisplayName(HANDLE_FEIGN_CLIENT_EXCEPTION + " - When FeignException is thrown then handle accordingly")
    @ParameterizedTest(name = "Test {index} => status={0} | expectedStatus={1} | expectedMessage={2}")
    @MethodSource("feignClientExceptionProvider")
    void testHandleFeignClientException(int status,
                                        HttpStatus expectedStatus,
                                        String expectedMessage,
                                        HandlerConfig handlerConfig) {

        FeignException ex = mock(FeignException.class);
        when(ex.status()).thenReturn(status);
        when(ex.getMessage()).thenReturn("Feign client error");

        feign.Request requestMock = mock(feign.Request.class);
        when(requestMock.headers()).thenReturn(new HashMap<>());
        when(ex.request()).thenReturn(requestMock);

        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            Function<FeignException, ?> messageFunction = handlerConfig.function();

            if (handlerConfig.returnsString()) {
                mockedStatic.when(() -> messageFunction.apply(ex)).thenReturn(expectedMessage);
            } else {
                Map<String, String> mockMessages = new HashMap<>();
                mockMessages.put("error", expectedMessage);
                mockedStatic.when(() -> messageFunction.apply(ex)).thenReturn(mockMessages);
            }

            ResponseEntity<?> responseEntity = exceptionHandler.handleFeignClientException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(expectedStatus, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);

            if (responseBody instanceof ErrorMultipleResponse errorMultipleResponse) {
                assertEquals(expectedStatus.value(), errorMultipleResponse.status());
                assertNotNull(errorMultipleResponse.messages());
                assertEquals(expectedMessage, errorMultipleResponse.messages().get("error"));
            } else if (responseBody instanceof ErrorSingleResponse errorSingleResponse) {
                assertEquals(expectedStatus.value(), errorSingleResponse.status());
                assertNotNull(errorSingleResponse.message());
                assertEquals(expectedMessage, errorSingleResponse.message());
            } else {
                fail("Unexpected response body type");
            }
        }
    }

    static Stream<Arguments> feignClientExceptionProvider() {
        return Stream.of(
                Arguments.of(401, HttpStatus.UNAUTHORIZED, "Custom unauthorized message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getUnauthorizedMessage, true)),
                Arguments.of(400, HttpStatus.BAD_REQUEST, "Custom bad request message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getBadRequestMessage, false)),
                Arguments.of(404, HttpStatus.NOT_FOUND, "Custom not found message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getNotFoundMessage, true)),
                Arguments.of(405, HttpStatus.METHOD_NOT_ALLOWED, "Custom method not allowed message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getMethodNotAllowedMessage, true)),
                Arguments.of(403, HttpStatus.FORBIDDEN, "Custom forbidden message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getForbiddenMessage, true)),
                Arguments.of(408, HttpStatus.REQUEST_TIMEOUT, "Custom timeout message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getTimeoutMessage, true)),
                Arguments.of(409, HttpStatus.CONFLICT, "Custom conflict message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getConflictMessage, true)),
                Arguments.of(-1, HttpStatus.INTERNAL_SERVER_ERROR, "Custom internal server error message1",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getInternalServerErrorMessage, true)),
                Arguments.of(500, HttpStatus.INTERNAL_SERVER_ERROR, "Custom internal server error message2",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getInternalServerErrorMessage, true)),
                Arguments.of(406, HttpStatus.NOT_ACCEPTABLE, "Custom not acceptable message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getHttpMediaTypeNotAcceptableException, true)),
                Arguments.of(415, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Custom unsupported media type message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getHttpMediaTypeNotSupportedException, true)),
                Arguments.of(413, HttpStatus.PAYLOAD_TOO_LARGE, "Custom payload too large message",
                        new HandlerConfig(ExceptionHandlerMessageHelper::getMaxUploadSizeExceededException, true))
        );
    }


}
