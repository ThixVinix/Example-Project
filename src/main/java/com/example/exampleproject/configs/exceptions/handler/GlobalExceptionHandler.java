package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.ErrorResponse;
import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.configs.exceptions.custom.DataIntegrityViolationException;
import com.example.exampleproject.configs.exceptions.custom.ResourceNotFoundException;
import com.example.exampleproject.configs.exceptions.custom.UnauthorizedException;
import com.example.exampleproject.configs.exceptions.handler.helper.ExceptionHandlerMessageHelper;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(Exception ex,
                                                                         WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getNotFoundMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(Exception ex,
                                                                                      WebRequest request) {
        log.error("HTTP request method not supported: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getMethodNotAllowedMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MissingRequestHeaderException.class,
            MissingPathVariableException.class,
            BusinessException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            HandlerMethodValidationException.class,
            BindException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception ex, WebRequest request) {
        log.error("Bad request: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getBadRequestMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(Exception ex, WebRequest request) {
        log.error("Unauthorized: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getUnauthorizedMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(Exception ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getForbiddenMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(Exception ex,
                                                                 WebRequest request) {
        log.error("Conflict: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getConflictMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorResponse> handleTimeoutException(Exception ex, WebRequest request) {
        log.error("Request timed out: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.REQUEST_TIMEOUT.value())
                .error(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getTimeoutMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.REQUEST_TIMEOUT);
    }


    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptableException(Exception ex, WebRequest request) {
        log.error("Http Media Type Not Acceptable: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .error(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getHttpMediaTypeNotAcceptableException(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(Exception ex, WebRequest request) {
        log.error("Http Media Type Not Supported: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getHttpMediaTypeNotSupportedException(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler({Exception.class, Throwable.class})
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getInternalServerErrorMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignClientException(FeignException e, WebRequest request) {
        String requestUri = request.getDescription(false);
        HttpStatus status = HttpStatus.resolve(e.status());
        int statusFeign = e.status();

        String responseBody = e.responseBody().isPresent() ? e.responseBody().get().toString() : "No response body";
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

        if (statusFeign == -1) {
            return this.handleGlobalException(e, request);
        }

        return switch (status) {
            case BAD_REQUEST -> this.handleBadRequestException(e, request);
            case UNAUTHORIZED -> this.handleUnauthorizedException(e, request);
            case FORBIDDEN -> this.handleForbiddenException(e, request);
            case NOT_FOUND -> this.handleResourceNotFoundException(e, request);
            case METHOD_NOT_ALLOWED -> this.handleHttpRequestMethodNotSupportedException(e, request);
            case NOT_ACCEPTABLE -> this.handleHttpMediaTypeNotAcceptableException(e, request);
            case REQUEST_TIMEOUT -> this.handleTimeoutException(e, request);
            case CONFLICT -> this.handleConflictException(e, request);
            case UNSUPPORTED_MEDIA_TYPE -> this.handleHttpMediaTypeNotSupportedException(e, request);
            case null, default -> this.handleGlobalException(e, request);
        };
    }
}

