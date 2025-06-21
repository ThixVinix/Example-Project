package com.example.exampleproject.configs.exceptions.handler;


import com.example.exampleproject.configs.exceptions.BaseError;
import com.example.exampleproject.configs.exceptions.ErrorMultipleResponse;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
import com.example.exampleproject.configs.exceptions.custom.DataIntegrityViolationException;
import com.example.exampleproject.configs.exceptions.custom.ResourceNotFoundException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
    private static final String HANDLE_UNAUTHORIZED_EXCEPTION = "handleUnauthorizedException";
    private static final String HANDLE_FORBIDDEN_EXCEPTION = "handleForbiddenException";
    private static final String HANDLE_CONFLICT_EXCEPTION = "handleConflictException";
    private static final String HANDLE_TIMEOUT_EXCEPTION = "handleTimeoutException";
    private static final String HANDLE_BAD_REQUEST_EXCEPTION = "handleBadRequestException";
    private static final String HANDLE_GLOBAL_EXCEPTION = "handleGlobalException";
    private static final String HANDLE_MAX_UPLOAD_SIZE_EXCEEDED_EXCEPTION = "handleMaxUploadSizeExceededException";
    private static final String HANDLE_METHOD_ARGUMENT_NOT_VALID = "handleMethodArgumentNotValid";
    private static final String HANDLE_HTTP_MESSAGE_NOT_READABLE = "handleHttpMessageNotReadable";
    private static final String HANDLE_MISSING_SERVLET_REQUEST_PARAMETER = "handleMissingServletRequestParameter";
    private static final String HANDLE_HTTP_REQUEST_METHOD_NOT_SUPPORTED = "handleHttpRequestMethodNotSupported";
    private static final String HANDLE_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE = "handleHttpMediaTypeNotAcceptable";
    private static final String HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED = "handleHttpMediaTypeNotSupported";
    private static final String HANDLE_MISSING_PATH_VARIABLE = "handleMissingPathVariable";
    private static final String HANDLE_HANDLER_METHOD_VALIDATION_EXCEPTION = "handleHandlerMethodValidationException";
    private static final String HANDLE_ASYNC_REQUEST_TIMEOUT_EXCEPTION = "handleAsyncRequestTimeoutException";
    private static final String HANDLE_NO_RESOURCE_FOUND_EXCEPTION = "handleNoResourceFoundException";
    private static final String HANDLE_SERVLET_REQUEST_BINDING_EXCEPTION = "handleServletRequestBindingException";
    private static final String HANDLE_METHOD_NOT_ALLOWED_EXCEPTION = "handleMethodNotAllowedException";
    private static final String HANDLE_NOT_ACCEPTABLE_EXCEPTION = "handleNotAcceptableException";
    private static final String HANDLE_UNSUPPORTED_MEDIA_TYPE_EXCEPTION = "handleUnsupportedMediaTypeException";
    private static final String HANDLE_SERVICE_UNAVAILABLE_EXCEPTION = "handleServiceUnavailableException";
    private static final String HANDLE_PAYLOAD_TOO_LARGE_EXCEPTION = "handlePayloadTooLargeException";
    private static final String HANDLE_FEIGN_CLIENT_EXCEPTION = "handleFeignClientException";

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
        Exception ex = new ResourceNotFoundException("Resource not found");
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
     * {@link GlobalExceptionHandler#handleUnauthorizedException(Exception, WebRequest)}
     */
    @Order(2)
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
            assertEquals(HttpStatus.UNAUTHORIZED.getReasonPhrase(), errorSingleResponse.error());
            assertEquals("/test/path", errorSingleResponse.path());
            assertNotNull(errorSingleResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleForbiddenException(Exception, WebRequest)}
     */
    @Order(3)
    @Tag(value = HANDLE_FORBIDDEN_EXCEPTION)
    @DisplayName(HANDLE_FORBIDDEN_EXCEPTION + " - When AccessDeniedException is thrown " +
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
            assertEquals(HttpStatus.FORBIDDEN.getReasonPhrase(), errorSingleResponse.error());
            assertEquals("/test/path", errorSingleResponse.path());
            assertNotNull(errorSingleResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleConflictException(Exception, WebRequest)}
     */
    @Order(4)
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
            assertEquals(HttpStatus.CONFLICT.getReasonPhrase(), errorSingleResponse.error());
            assertEquals("/test/path", errorSingleResponse.path());
            assertNotNull(errorSingleResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleTimeoutException(Exception, WebRequest)}
     */
    @Order(5)
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
            assertEquals(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase(), errorSingleResponse.error());
            assertEquals("/test/path", errorSingleResponse.path());
            assertNotNull(errorSingleResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleBadRequestException(Exception, WebRequest)}
     */
    @Order(6)
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
    @Order(7)
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
    @Order(8)
    @Tag(value = HANDLE_GLOBAL_EXCEPTION)
    @DisplayName(HANDLE_GLOBAL_EXCEPTION +
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
     * {@link GlobalExceptionHandler#handleMaxUploadSizeExceededException(MaxUploadSizeExceededException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(9)
    @Tag(value = HANDLE_MAX_UPLOAD_SIZE_EXCEEDED_EXCEPTION)
    @DisplayName(HANDLE_MAX_UPLOAD_SIZE_EXCEEDED_EXCEPTION + " - When MaxUploadSizeExceededException is thrown then " +
            "return Payload Too Large status")
    @Test
    void testHandleMaxUploadSizeExceededException() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(5000000); // limite de 5MB simulado
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.PAYLOAD_TOO_LARGE;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/upload-path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() ->
                            ExceptionHandlerMessageHelper.getMaxUploadSizeExceededException(ex))
                    .thenReturn("The uploaded file size exceeds the allowed limit of 5MB.");

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleMaxUploadSizeExceededException(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorSingleResponse.class, responseBody);

            ErrorSingleResponse errorResponse = (ErrorSingleResponse) responseBody;
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE.value(), errorResponse.status());
            assertEquals("The uploaded file size exceeds the allowed limit of 5MB.", errorResponse.message());
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/upload-path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleMethodArgumentNotValid(MethodArgumentNotValidException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(10)
    @Tag(value = HANDLE_METHOD_ARGUMENT_NOT_VALID)
    @DisplayName(HANDLE_METHOD_ARGUMENT_NOT_VALID + " - When MethodArgumentNotValidException is thrown then " +
            "return Bad Request status")
    @Test
    void testHandleMethodArgumentNotValid() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("field1", "Field validation error");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getBadRequestMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleMethodArgumentNotValid(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorMultipleResponse.class, responseBody);

            ErrorMultipleResponse errorResponse = (ErrorMultipleResponse) responseBody;
            assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
            assertEquals("Field validation error", errorResponse.messages().get("field1"));
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleHttpMessageNotReadable(HttpMessageNotReadableException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(11)
    @Tag(value = HANDLE_HTTP_MESSAGE_NOT_READABLE)
    @DisplayName(HANDLE_HTTP_MESSAGE_NOT_READABLE + " - When HttpMessageNotReadableException is thrown then " +
            "return Bad Request status")
    @Test
    void testHandleHttpMessageNotReadable() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("message", "Message not readable error");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getBadRequestMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleHttpMessageNotReadable(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorSingleResponse.class, responseBody);

            ErrorSingleResponse errorResponse = (ErrorSingleResponse) responseBody;
            assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
            assertEquals("Message not readable error", errorResponse.message());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleMissingServletRequestParameter(MissingServletRequestParameterException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(12)
    @Tag(value = HANDLE_MISSING_SERVLET_REQUEST_PARAMETER)
    @DisplayName(HANDLE_MISSING_SERVLET_REQUEST_PARAMETER + " - When MissingServletRequestParameterException is thrown then " +
            "return Bad Request status")
    @Test
    void testHandleMissingServletRequestParameter() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("param", "String");
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("param", "Missing parameter error");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getBadRequestMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleMissingServletRequestParameter(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorMultipleResponse.class, responseBody);

            ErrorMultipleResponse errorResponse = (ErrorMultipleResponse) responseBody;
            assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
            assertEquals("Missing parameter error", errorResponse.messages().get("param"));
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(13)
    @Tag(value = HANDLE_HTTP_REQUEST_METHOD_NOT_SUPPORTED)
    @DisplayName(HANDLE_HTTP_REQUEST_METHOD_NOT_SUPPORTED + " - When HttpRequestMethodNotSupportedException is thrown then " +
            "return Method Not Allowed status")
    @Test
    void testHandleHttpRequestMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.METHOD_NOT_ALLOWED;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getMethodNotAllowedMessage(ex))
                    .thenReturn("Method not allowed error");

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleHttpRequestMethodNotSupported(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorSingleResponse.class, responseBody);

            ErrorSingleResponse errorResponse = (ErrorSingleResponse) responseBody;
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), errorResponse.status());
            assertEquals("Method not allowed error", errorResponse.message());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(14)
    @Tag(value = HANDLE_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE)
    @DisplayName(HANDLE_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE + " - When HttpMediaTypeNotAcceptableException is thrown then " +
            "return Not Acceptable status")
    @Test
    void testHandleHttpMediaTypeNotAcceptable() {
        HttpMediaTypeNotAcceptableException ex = new HttpMediaTypeNotAcceptableException("Media type not acceptable");
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.NOT_ACCEPTABLE;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getHttpMediaTypeNotAcceptableException(ex))
                    .thenReturn("Media type not acceptable error");

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleHttpMediaTypeNotAcceptable(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorSingleResponse.class, responseBody);

            ErrorSingleResponse errorResponse = (ErrorSingleResponse) responseBody;
            assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), errorResponse.status());
            assertEquals("Media type not acceptable error", errorResponse.message());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(15)
    @Tag(value = HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED)
    @DisplayName(HANDLE_HTTP_MEDIA_TYPE_NOT_SUPPORTED + " - When HttpMediaTypeNotSupportedException is thrown then " +
            "return Unsupported Media Type status")
    @Test
    void testHandleHttpMediaTypeNotSupported() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("Media type not supported");
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getHttpMediaTypeNotSupportedException(ex))
                    .thenReturn("Media type not supported error");

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleHttpMediaTypeNotSupported(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorSingleResponse.class, responseBody);

            ErrorSingleResponse errorResponse = (ErrorSingleResponse) responseBody;
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), errorResponse.status());
            assertEquals("Media type not supported error", errorResponse.message());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleMissingPathVariable(MissingPathVariableException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(16)
    @Tag(value = HANDLE_MISSING_PATH_VARIABLE)
    @DisplayName(HANDLE_MISSING_PATH_VARIABLE + " - When MissingPathVariableException is thrown then " +
            "return Bad Request status")
    @Test
    void testHandleMissingPathVariable() {
        MissingPathVariableException ex = mock(MissingPathVariableException.class);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");
        when(ex.getVariableName()).thenReturn("id");

        ResponseEntity<Object> responseEntity =
                exceptionHandler.handleMissingPathVariable(ex, headers, status, request);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        Object responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertInstanceOf(ErrorSingleResponse.class, responseBody);

        ErrorSingleResponse errorResponse = (ErrorSingleResponse) responseBody;
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
        assertEquals("Missing path variable: id", errorResponse.message());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), errorResponse.error());
        assertEquals("/test/path", errorResponse.path());
        assertNotNull(errorResponse.timestamp());
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleHandlerMethodValidationException(HandlerMethodValidationException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(17)
    @Tag(value = HANDLE_HANDLER_METHOD_VALIDATION_EXCEPTION)
    @DisplayName(HANDLE_HANDLER_METHOD_VALIDATION_EXCEPTION + " - When HandlerMethodValidationException is thrown then " +
            "return Bad Request status")
    @Test
    void testHandleHandlerMethodValidationException() {
        HandlerMethodValidationException ex = mock(HandlerMethodValidationException.class);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("validation", "Handler method validation error");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getBadRequestMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleHandlerMethodValidationException(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorMultipleResponse.class, responseBody);

            ErrorMultipleResponse errorResponse = (ErrorMultipleResponse) responseBody;
            assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
            assertEquals("Handler method validation error", errorResponse.messages().get("validation"));
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleAsyncRequestTimeoutException(AsyncRequestTimeoutException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(18)
    @Tag(value = HANDLE_ASYNC_REQUEST_TIMEOUT_EXCEPTION)
    @DisplayName(HANDLE_ASYNC_REQUEST_TIMEOUT_EXCEPTION + " - When AsyncRequestTimeoutException is thrown then " +
            "return Service Unavailable status")
    @Test
    void testHandleAsyncRequestTimeoutException() {
        AsyncRequestTimeoutException ex = new AsyncRequestTimeoutException();
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.SERVICE_UNAVAILABLE;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getServiceUnavailableMessage(ex))
                    .thenReturn("Service unavailable error");

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleAsyncRequestTimeoutException(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorSingleResponse.class, responseBody);

            ErrorSingleResponse errorResponse = (ErrorSingleResponse) responseBody;
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), errorResponse.status());
            assertEquals("Service unavailable error", errorResponse.message());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleNoResourceFoundException(NoResourceFoundException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(19)
    @Tag(value = HANDLE_NO_RESOURCE_FOUND_EXCEPTION)
    @DisplayName(HANDLE_NO_RESOURCE_FOUND_EXCEPTION + " - When NoResourceFoundException is thrown then " +
            "return Not Found status")
    @Test
    void testHandleNoResourceFoundException() {
        NoResourceFoundException ex = mock(NoResourceFoundException.class);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.NOT_FOUND;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getNotFoundMessage(ex))
                    .thenReturn("No resource found error");

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleNoResourceFoundException(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorSingleResponse.class, responseBody);

            ErrorSingleResponse errorResponse = (ErrorSingleResponse) responseBody;
            assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.status());
            assertEquals("No resource found error", errorResponse.message());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleServletRequestBindingException(ServletRequestBindingException, HttpHeaders, HttpStatusCode, WebRequest)}
     */
    @Order(20)
    @Tag(value = HANDLE_SERVLET_REQUEST_BINDING_EXCEPTION)
    @DisplayName(HANDLE_SERVLET_REQUEST_BINDING_EXCEPTION + " - When ServletRequestBindingException is thrown then " +
            "return Bad Request status")
    @Test
    void testHandleServletRequestBindingException() {
        ServletRequestBindingException ex = mock(ServletRequestBindingException.class);
        HttpHeaders headers = new HttpHeaders();
        HttpStatusCode status = HttpStatus.BAD_REQUEST;
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        Map<String, String> mockMessages = new HashMap<>();
        mockMessages.put("binding", "Servlet request binding error");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getBadRequestMessage(ex)).thenReturn(mockMessages);

            ResponseEntity<Object> responseEntity =
                    exceptionHandler.handleServletRequestBindingException(ex, headers, status, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

            Object responseBody = responseEntity.getBody();
            assertNotNull(responseBody);
            assertInstanceOf(ErrorMultipleResponse.class, responseBody);

            ErrorMultipleResponse errorResponse = (ErrorMultipleResponse) responseBody;
            assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
            assertEquals("Servlet request binding error", errorResponse.messages().get("binding"));
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleMethodNotAllowedException(Exception, WebRequest)}
     */
    @Order(21)
    @Tag(value = HANDLE_METHOD_NOT_ALLOWED_EXCEPTION)
    @DisplayName(HANDLE_METHOD_NOT_ALLOWED_EXCEPTION + " - When Exception is thrown then " +
            "return Method Not Allowed status")
    @Test
    void testHandleMethodNotAllowedException() {
        Exception ex = new Exception("Method not allowed");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getMethodNotAllowedMessage(ex))
                    .thenReturn("Custom method not allowed message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleMethodNotAllowedException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());

            ErrorSingleResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), errorResponse.status());
            assertEquals("Custom method not allowed message", errorResponse.message());
            assertEquals(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleNotAcceptableException(Exception, WebRequest)}
     */
    @Order(22)
    @Tag(value = HANDLE_NOT_ACCEPTABLE_EXCEPTION)
    @DisplayName(HANDLE_NOT_ACCEPTABLE_EXCEPTION + " - When Exception is thrown then " +
            "return Not Acceptable status")
    @Test
    void testHandleNotAcceptableException() {
        Exception ex = new Exception("Not acceptable");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getHttpMediaTypeNotAcceptableException(ex))
                    .thenReturn("Custom not acceptable message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleNotAcceptableException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());

            ErrorSingleResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), errorResponse.status());
            assertEquals("Custom not acceptable message", errorResponse.message());
            assertEquals(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleUnsupportedMediaTypeException(Exception, WebRequest)}
     */
    @Order(23)
    @Tag(value = HANDLE_UNSUPPORTED_MEDIA_TYPE_EXCEPTION)
    @DisplayName(HANDLE_UNSUPPORTED_MEDIA_TYPE_EXCEPTION + " - When Exception is thrown then " +
            "return Unsupported Media Type status")
    @Test
    void testHandleUnsupportedMediaTypeException() {
        Exception ex = new Exception("Unsupported media type");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getHttpMediaTypeNotSupportedException(ex))
                    .thenReturn("Custom unsupported media type message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleUnsupportedMediaTypeException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, responseEntity.getStatusCode());

            ErrorSingleResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), errorResponse.status());
            assertEquals("Custom unsupported media type message", errorResponse.message());
            assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleServiceUnavailableException(Exception, WebRequest)}
     */
    @Order(24)
    @Tag(value = HANDLE_SERVICE_UNAVAILABLE_EXCEPTION)
    @DisplayName(HANDLE_SERVICE_UNAVAILABLE_EXCEPTION + " - When Exception is thrown then " +
            "return Service Unavailable status")
    @Test
    void testHandleServiceUnavailableException() {
        Exception ex = new Exception("Service unavailable");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getServiceUnavailableMessage(ex))
                    .thenReturn("Custom service unavailable message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handleServiceUnavailableException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, responseEntity.getStatusCode());

            ErrorSingleResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), errorResponse.status());
            assertEquals("Custom service unavailable message", errorResponse.message());
            assertEquals(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handlePayloadTooLargeException(Exception, WebRequest)}
     */
    @Order(25)
    @Tag(value = HANDLE_PAYLOAD_TOO_LARGE_EXCEPTION)
    @DisplayName(HANDLE_PAYLOAD_TOO_LARGE_EXCEPTION + " - When Exception is thrown then " +
            "return Payload Too Large status")
    @Test
    void testHandlePayloadTooLargeException() {
        Exception ex = new Exception("Payload too large");
        WebRequest request = mock(WebRequest.class);

        when(request.getDescription(false)).thenReturn("/test/path");

        try (var mockedStatic = mockStatic(ExceptionHandlerMessageHelper.class)) {
            mockedStatic.when(() -> ExceptionHandlerMessageHelper.getMaxUploadSizeExceededException(ex))
                    .thenReturn("Custom payload too large message");

            ResponseEntity<ErrorSingleResponse> responseEntity =
                    exceptionHandler.handlePayloadTooLargeException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, responseEntity.getStatusCode());

            ErrorSingleResponse errorResponse = responseEntity.getBody();
            assertNotNull(errorResponse);
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE.value(), errorResponse.status());
            assertEquals("Custom payload too large message", errorResponse.message());
            assertEquals(HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase(), errorResponse.error());
            assertEquals("/test/path", errorResponse.path());
            assertNotNull(errorResponse.timestamp());
        }
    }

    /**
     * Method test for
     * {@link GlobalExceptionHandler#handleFeignClientException(FeignException, WebRequest)}
     */
    @Order(26)
    @Tag(value = HANDLE_FEIGN_CLIENT_EXCEPTION)
    @DisplayName(HANDLE_FEIGN_CLIENT_EXCEPTION + " - When FeignException is thrown then handle accordingly")
    @ParameterizedTest(name = "Test {index} => status={0} | expectedStatus={1} | expectedMessage={2}")
    @MethodSource("feignClientExceptionProvider")
    void testHandleFeignClientException(int status,
                                        HttpStatus expectedStatus,
                                        String expectedMessage,
                                        GlobalExceptionHandlerTest.HandlerConfig handlerConfig) {

        FeignException ex = mock(FeignException.class);
        when(ex.status()).thenReturn(status);
        when(ex.getMessage()).thenReturn("Feign client error");

        feign.Request requestMock = mock(feign.Request.class);
        when(requestMock.headers()).thenReturn(new HashMap<>());
        when(ex.request()).thenReturn(requestMock);
        when(ex.responseBody()).thenReturn(java.util.Optional.of(java.nio.ByteBuffer.wrap("response body".getBytes())));
        when(ex.responseHeaders()).thenReturn(new HashMap<>());

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

            ResponseEntity<? extends BaseError> responseEntity = exceptionHandler.handleFeignClientException(ex, request);

            assertNotNull(responseEntity);
            assertEquals(expectedStatus, responseEntity.getStatusCode());

            BaseError responseBody = responseEntity.getBody();
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
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getUnauthorizedMessage, true)),
                Arguments.of(400, HttpStatus.BAD_REQUEST, "Custom bad request message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getBadRequestMessage, false)),
                Arguments.of(404, HttpStatus.NOT_FOUND, "Custom not found message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getNotFoundMessage, true)),
                Arguments.of(405, HttpStatus.METHOD_NOT_ALLOWED, "Custom method not allowed message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getMethodNotAllowedMessage, true)),
                Arguments.of(403, HttpStatus.FORBIDDEN, "Custom forbidden message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getForbiddenMessage, true)),
                Arguments.of(408, HttpStatus.REQUEST_TIMEOUT, "Custom timeout message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getTimeoutMessage, true)),
                Arguments.of(409, HttpStatus.CONFLICT, "Custom conflict message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getConflictMessage, true)),
                Arguments.of(-1, HttpStatus.INTERNAL_SERVER_ERROR, "Custom internal server error message1",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getInternalServerErrorMessage, true)),
                Arguments.of(500, HttpStatus.INTERNAL_SERVER_ERROR, "Custom internal server error message2",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getInternalServerErrorMessage, true)),
                Arguments.of(406, HttpStatus.NOT_ACCEPTABLE, "Custom not acceptable message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getHttpMediaTypeNotAcceptableException, true)),
                Arguments.of(415, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Custom unsupported media type message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getHttpMediaTypeNotSupportedException, true)),
                Arguments.of(413, HttpStatus.PAYLOAD_TOO_LARGE, "Custom payload too large message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getMaxUploadSizeExceededException, true)),
                Arguments.of(503, HttpStatus.SERVICE_UNAVAILABLE, "Custom service unavailable message",
                        new GlobalExceptionHandlerTest.HandlerConfig(ExceptionHandlerMessageHelper::getServiceUnavailableMessage, true))
        );
    }


}
