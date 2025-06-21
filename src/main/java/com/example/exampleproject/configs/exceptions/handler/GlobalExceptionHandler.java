package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.BaseError;
import com.example.exampleproject.configs.exceptions.ErrorMultipleResponse;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.configs.exceptions.custom.DataIntegrityViolationException;
import com.example.exampleproject.configs.exceptions.custom.ResourceNotFoundException;
import com.example.exampleproject.configs.exceptions.custom.UnauthorizedException;
import com.example.exampleproject.configs.exceptions.handler.helper.ExceptionHandlerMessageHelper;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static java.util.Objects.isNull;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ErrorSingleResponse> handleResourceNotFoundException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.NOT_FOUND,
                "Resource not found: {}",
                ExceptionHandlerMessageHelper::getNotFoundMessage);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<ErrorSingleResponse> handleUnauthorizedException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.UNAUTHORIZED,
                "Unauthorized: {}",
                ExceptionHandlerMessageHelper::getUnauthorizedMessage);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorSingleResponse> handleForbiddenException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.FORBIDDEN,
                "Access denied: {}",
                ExceptionHandlerMessageHelper::getForbiddenMessage);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorSingleResponse> handleConflictException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.CONFLICT,
                "Conflict: {}",
                ExceptionHandlerMessageHelper::getConflictMessage);
    }

    @ExceptionHandler(TimeoutException.class)
    protected ResponseEntity<ErrorSingleResponse> handleTimeoutException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.REQUEST_TIMEOUT,
                "Request timed out: {}",
                ExceptionHandlerMessageHelper::getTimeoutMessage);
    }

    @ExceptionHandler({
            BusinessException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class
    })
    @SuppressWarnings("squid:S1452")
    protected ResponseEntity<? extends BaseError> handleBadRequestException(Exception ex, WebRequest request) {
        return handleMultipleErrorResponse(
                ex,
                request,
                ExceptionHandlerMessageHelper::getBadRequestMessage);
    }

    @ExceptionHandler({Exception.class, Throwable.class})
    protected ResponseEntity<ErrorSingleResponse> handleGlobalException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: {}",
                ExceptionHandlerMessageHelper::getInternalServerErrorMessage);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(@NonNull MaxUploadSizeExceededException ex,
                                                                          @NonNull HttpHeaders headers,
                                                                          @NonNull HttpStatusCode status,
                                                                          @NonNull WebRequest request) {
        return handleOverrideSingleErrorResponse(
                ex,
                headers,
                status,
                request,
                "Max Upload Size Exceeded: {}",
                ExceptionHandlerMessageHelper::getMaxUploadSizeExceededException);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        return handleOverrideMultipleErrorResponse(
                ex,
                headers,
                status,
                request,
                "Method argument not valid: {}",
                ExceptionHandlerMessageHelper::getBadRequestMessage);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        return handleOverrideMultipleErrorResponse(
                ex,
                headers,
                status,
                request,
                "HTTP message not readable: {}",
                ExceptionHandlerMessageHelper::getBadRequestMessage);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        return handleOverrideMultipleErrorResponse(
                ex,
                headers,
                status,
                request,
                "Missing servlet request parameter: {}",
                ExceptionHandlerMessageHelper::getBadRequestMessage);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(@NonNull
                                                                         HttpRequestMethodNotSupportedException ex,
                                                                         @NonNull HttpHeaders headers,
                                                                         @NonNull HttpStatusCode status,
                                                                         @NonNull WebRequest request) {
        return handleOverrideSingleErrorResponse(
                ex,
                headers,
                status,
                request,
                "HTTP request method not supported: {}",
                ExceptionHandlerMessageHelper::getMethodNotAllowedMessage);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(@NonNull HttpMediaTypeNotAcceptableException ex,
                                                                      @NonNull HttpHeaders headers,
                                                                      @NonNull HttpStatusCode status,
                                                                      @NonNull WebRequest request) {
        return handleOverrideSingleErrorResponse(
                ex,
                headers,
                status,
                request,
                "HTTP media type not acceptable: {}",
                ExceptionHandlerMessageHelper::getHttpMediaTypeNotAcceptableException);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(@NonNull HttpMediaTypeNotSupportedException ex,
                                                                     @NonNull HttpHeaders headers,
                                                                     @NonNull HttpStatusCode status,
                                                                     @NonNull WebRequest request) {
        return handleOverrideSingleErrorResponse(
                ex,
                headers,
                status,
                request,
                "HTTP media type not supported: {}",
                ExceptionHandlerMessageHelper::getHttpMediaTypeNotSupportedException);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(@NonNull MissingPathVariableException ex,
                                                               @NonNull HttpHeaders headers,
                                                               @NonNull HttpStatusCode status,
                                                               @NonNull WebRequest request) {
        return handleOverrideSingleErrorResponse(
                ex,
                headers,
                status,
                request,
                "Missing path variable: {}",
                e -> "Missing path variable: " + ((MissingPathVariableException) e).getVariableName());
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(@NonNull
                                                                            HandlerMethodValidationException ex,
                                                                            @NonNull HttpHeaders headers,
                                                                            @NonNull HttpStatusCode status,
                                                                            @NonNull WebRequest request) {
        return handleOverrideMultipleErrorResponse(
                ex,
                headers,
                status,
                request,
                "Handler method validation exception: {}",
                ExceptionHandlerMessageHelper::getBadRequestMessage);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(@NonNull AsyncRequestTimeoutException ex,
                                                                        @NonNull HttpHeaders headers,
                                                                        @NonNull HttpStatusCode status,
                                                                        @NonNull WebRequest request) {
        return handleOverrideSingleErrorResponse(
                ex,
                headers,
                status,
                request,
                "Async request timeout: {}",
                ExceptionHandlerMessageHelper::getServiceUnavailableMessage);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(@NonNull NoResourceFoundException ex,
                                                                    @NonNull HttpHeaders headers,
                                                                    @NonNull HttpStatusCode status,
                                                                    @NonNull WebRequest request) {
        return handleOverrideSingleErrorResponse(
                ex,
                headers,
                status,
                request,
                "No resource found: {}",
                ExceptionHandlerMessageHelper::getNotFoundMessage);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(@NonNull ServletRequestBindingException ex,
                                                                          @NonNull HttpHeaders headers,
                                                                          @NonNull HttpStatusCode status,
                                                                          @NonNull WebRequest request) {
        return handleOverrideMultipleErrorResponse(
                ex,
                headers,
                status,
                request,
                "Servlet request binding exception: {}",
                ExceptionHandlerMessageHelper::getBadRequestMessage);
    }

    protected ResponseEntity<ErrorSingleResponse> handleMethodNotAllowedException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.METHOD_NOT_ALLOWED,
                "Method not allowed: {}",
                ExceptionHandlerMessageHelper::getMethodNotAllowedMessage);
    }

    protected ResponseEntity<ErrorSingleResponse> handleNotAcceptableException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.NOT_ACCEPTABLE,
                "Not acceptable: {}",
                ExceptionHandlerMessageHelper::getHttpMediaTypeNotAcceptableException);
    }

    protected ResponseEntity<ErrorSingleResponse> handleUnsupportedMediaTypeException(Exception ex,
                                                                                      WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported media type: {}",
                ExceptionHandlerMessageHelper::getHttpMediaTypeNotSupportedException);
    }

    protected ResponseEntity<ErrorSingleResponse> handleServiceUnavailableException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service unavailable: {}",
                ExceptionHandlerMessageHelper::getServiceUnavailableMessage);
    }

    protected ResponseEntity<ErrorSingleResponse> handlePayloadTooLargeException(Exception ex, WebRequest request) {
        return handleSingleErrorResponse(
                ex,
                request,
                HttpStatus.PAYLOAD_TOO_LARGE,
                "Payload too large: {}",
                ExceptionHandlerMessageHelper::getMaxUploadSizeExceededException);
    }

    @SuppressWarnings("squid:S1452")
    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<? extends BaseError> handleFeignClientException(FeignException e, WebRequest request) {
        String requestUri = request.getDescription(false);
        HttpStatus status = HttpStatus.resolve(e.status());
        int statusFeign = e.status();

        logFeignErrorDetails(e, requestUri, status);

        if (statusFeign == -1 || isNull(status)) {
            return this.handleGlobalException(e, request);
        }

        return getResponseByStatus(status, e, request);
    }

    private void logFeignErrorDetails(FeignException e, String requestUri, HttpStatus status) {
        String responseBody = e.responseBody().map(Object::toString).orElse("No response body");
        String requestHeaders = e.request().headers().toString();
        String responseHeaders = e.responseHeaders().toString();

        log.error("""
                        FEIGN CLIENT ERROR:
                        URI: {}
                        Status: {}
                        Request Headers: {}
                        Response Headers: {}
                        Response Body: {}
                        """,
                requestUri,
                status != null ? status.name() : "Unknown Status",
                requestHeaders,
                responseHeaders,
                responseBody);
    }

    private ResponseEntity<? extends BaseError> getResponseByStatus(HttpStatus status,
                                                                    FeignException e,
                                                                    WebRequest request) {
        return switch (status) {
            case BAD_REQUEST -> this.handleBadRequestException(e, request);
            case UNAUTHORIZED -> this.handleUnauthorizedException(e, request);
            case FORBIDDEN -> this.handleForbiddenException(e, request);
            case NOT_FOUND -> this.handleResourceNotFoundException(e, request);
            case METHOD_NOT_ALLOWED -> this.handleMethodNotAllowedException(e, request);
            case NOT_ACCEPTABLE -> this.handleNotAcceptableException(e, request);
            case REQUEST_TIMEOUT -> this.handleTimeoutException(e, request);
            case CONFLICT -> this.handleConflictException(e, request);
            case UNSUPPORTED_MEDIA_TYPE -> this.handleUnsupportedMediaTypeException(e, request);
            case PAYLOAD_TOO_LARGE -> this.handlePayloadTooLargeException(e, request);
            case SERVICE_UNAVAILABLE -> this.handleServiceUnavailableException(e, request);
            default -> this.handleGlobalException(e, request);
        };
    }

    /**
     * Helper method to handle exceptions that return a single error response.
     *
     * @param ex      The exception
     * @param request The WebRequest
     * @param status  The HTTP status
     * @param logMessage The log message
     * @param messageSupplier A function that supplies the error message
     * @return A ResponseEntity with the error response
     */
    private ResponseEntity<ErrorSingleResponse> handleSingleErrorResponse(
            Exception ex,
            WebRequest request,
            HttpStatus status,
            String logMessage,
            Function<Exception, String> messageSupplier) {

        log.error(logMessage, ex.getMessage(), ex);

        String path = getRequestPath(request);
        String message = messageSupplier.apply(ex);
        ErrorSingleResponse errorResponse = createErrorSingleResponse(status, message, path);

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Helper method to handle exceptions that return multiple error responses.
     *
     * @param ex               The exception
     * @param request          The WebRequest
     * @param messagesSupplier A function that supplies the error messages map
     * @return A ResponseEntity with the appropriate error response
     */
    private ResponseEntity<? extends BaseError> handleMultipleErrorResponse(
            Exception ex,
            WebRequest request,
            Function<Exception, Map<String, String>> messagesSupplier) {

        log.error("Bad request: {}", ex.getMessage(), ex);

        String path = getRequestPath(request);
        Map<String, String> messages = messagesSupplier.apply(ex);
        BaseError body = createAppropriateErrorResponse(messages, path);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * Extracts the request path from the WebRequest object.
     *
     * @param request The WebRequest object
     * @return The extracted path
     */
    private String getRequestPath(WebRequest request) {
        return request.getDescription(false);
    }

    /**
     * Determines if the response should be a single ({@link ErrorSingleResponse})
     * or multiple ({@link ErrorMultipleResponse}) error response.
     *
     * @param messages The error messages map
     * @param path     The request path
     * @return The appropriate BaseError object ({@link ErrorSingleResponse} or {@link ErrorMultipleResponse})
     */
    private BaseError createAppropriateErrorResponse(Map<String, String> messages, String path) {
        final String DEFAULT_MESSAGE_KEY = "message";

        if (messages.size() == NumberUtils.INTEGER_ONE && messages.containsKey(DEFAULT_MESSAGE_KEY)) {
            return createErrorSingleResponse(HttpStatus.BAD_REQUEST, messages.get(DEFAULT_MESSAGE_KEY), path);
        } else {
            return createErrorMultipleResponse(messages, path);
        }
    }

    /**
     * Creates an {@link ErrorSingleResponse} object with the given parameters.
     *
     * @param status  The HTTP status
     * @param message The error message
     * @param path    The request path
     * @return The created {@link ErrorSingleResponse} object
     */
    private ErrorSingleResponse createErrorSingleResponse(HttpStatus status, String message, String path) {
        return ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }

    /**
     * Creates an {@link ErrorMultipleResponse} object with the given parameters.
     *
     * @param messages The error messages map
     * @param path     The request path
     * @return The created {@link ErrorMultipleResponse} object
     */
    private ErrorMultipleResponse createErrorMultipleResponse(Map<String, String> messages,
                                                              String path) {
        return ErrorMultipleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .messages(messages)
                .path(path)
                .build();
    }

    /**
     * Handles an override for a single error response in a standardized format.
     * This method logs the error, extracts the request path, generates a structured
     * error response, and constructs a ResponseEntity using the provided HTTP headers
     * and status.
     *
     * @param ex              The exception that triggered the error handling.
     * @param headers         The headers to be included in the HTTP response.
     * @param status          The HTTP status code of the response, encapsulated in
     *                        {@link HttpStatusCode}.
     * @param request         The {@link WebRequest} that triggered the error.
     * @param logMessage      The log message to be recorded with the error.
     * @param messageSupplier A function to generate the error message based on the exception.
     * @return A {@link ResponseEntity} containing the structured single error response
     *         and appropriate HTTP status.
     */
    private ResponseEntity<Object> handleOverrideSingleErrorResponse(
            Exception ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request,
            String logMessage,
            Function<Exception, String> messageSupplier) {

        log.error(logMessage, ex.getMessage(), ex);

        String path = getRequestPath(request);
        String message = messageSupplier.apply(ex);

        HttpStatus httpStatus = resolveHttpStatus(status);

        ErrorSingleResponse errorResponse = createErrorSingleResponse(httpStatus, message, path);

        return new ResponseEntity<>(errorResponse, headers, httpStatus);
    }

    /**
     * Handles an override for error responses with multiple error messages in a standardized format.
     * This method logs the error, extracts the request path, generates a structured error response with multiple
     * messages, and constructs a ResponseEntity using the provided HTTP headers and status.
     *
     * @param ex               The exception that triggered the error handling.
     * @param headers          The headers to be included in the HTTP response.
     * @param status           The HTTP status code of the response.
     * @param request          The WebRequest that triggered the error.
     * @param logMessage       The log message to be recorded with the error.
     * @param messagesSupplier A function to generate a map of error messages based on the exception.
     * @return A ResponseEntity containing the structured error response and appropriate HTTP status.
     */
    private ResponseEntity<Object> handleOverrideMultipleErrorResponse(
            Exception ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request,
            String logMessage,
            Function<Exception, Map<String, String>> messagesSupplier) {

        log.error(logMessage, ex.getMessage(), ex);

        String path = getRequestPath(request);
        Map<String, String> messages = messagesSupplier.apply(ex);
        BaseError body = createAppropriateErrorResponse(messages, path);

        HttpStatus httpStatus = resolveHttpStatus(status);

        return new ResponseEntity<>(body, headers, httpStatus);
    }

    /**
     * Resolves a {@link HttpStatus} object from a given {@link HttpStatusCode}.
     * If the resolution fails, the default value of {@link HttpStatus#INTERNAL_SERVER_ERROR} is returned.
     *
     * @param status The {@link HttpStatusCode} to be resolved into a {@link HttpStatus}.
     * @return The resolved {@link HttpStatus}, or {@link HttpStatus#INTERNAL_SERVER_ERROR}
     * if resolution is unsuccessful.
     */
    private static HttpStatus resolveHttpStatus(HttpStatusCode status) {
        HttpStatus httpStatus = HttpStatus.resolve(status.value());

        if (isNull(httpStatus)) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return httpStatus;
    }
}
