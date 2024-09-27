package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.ErrorMapResponse;
import com.example.exampleproject.configs.exceptions.ErrorResponse;
import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.configs.exceptions.custom.ResourceNotFoundException;
import com.example.exampleproject.utils.messages.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                         WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMapResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
                                                                                  WebRequest request) {
        Locale locale = getLocaleFromRequest(request);

        Map<String, String> errorMessages = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ?
                                error.getDefaultMessage() : MessageUtils.getMessage(
                                "argument.type.invalid", locale),
                        (existingValue, newValue) -> {
                            if (existingValue.endsWith(".")) {
                                existingValue =
                                        existingValue.substring(0, existingValue.length() - 1) + "; " + newValue;
                            } else {
                                existingValue = existingValue + "; " + newValue;
                            }
                            return existingValue;
                        }
                ));

        ErrorMapResponse errorResponse = ErrorMapResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .messages(errorMessages)
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .message(ex.getMethod())
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            BusinessException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception ex, WebRequest request) {
        Locale locale = getLocaleFromRequest(request);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(getBadRequestMessage(ex, locale))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class, Throwable.class})
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        Locale locale = getLocaleFromRequest(request);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage() != null ? ex.getMessage() : MessageUtils.getMessage(
                        "unknown.exception.error", locale))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Locale getLocaleFromRequest(WebRequest request) {
        String lang = request.getParameter("lang");
        return (lang != null) ? Locale.forLanguageTag(lang) : Locale.getDefault();
    }

    private String getBadRequestMessage(Exception ex, Locale locale) {
        switch (ex) {
            case HttpMessageNotReadableException _ -> {
                return MessageUtils.getMessage("json.malformed", locale);
            }
            case MissingServletRequestParameterException missingEx -> {
                return MessageUtils.getMessage("missing.parameter", locale, missingEx.getParameterName());
            }
            case MethodArgumentTypeMismatchException mismatchEx -> {
                return getMismatchMessage(mismatchEx, locale);
            }
            case null, default -> {
                if (ex != null && ex.getMessage() != null) {
                    return ex.getMessage();
                } else {
                    return MessageUtils.getMessage("unknown.bad.request.error", locale);
                }
            }
        }
    }

    private String getMismatchMessage(MethodArgumentTypeMismatchException mismatchEx, Locale locale) {
        String expectedTypeName = (mismatchEx.getRequiredType() != null) ?
                mismatchEx.getRequiredType().getSimpleName() : null;

        return switch (expectedTypeName) {
            case null -> MessageUtils.getMessage(
                    "argument.type.mismatch.without.format",
                    locale,
                    mismatchEx.getName(),
                    mismatchEx.getValue());
            case "LocalDate", "LocalDateTime", "Date", "ZonedDateTime" -> handleDateMismatch(mismatchEx, locale);
            default -> MessageUtils.getMessage(
                    "argument.type.mismatch.default",
                    locale, mismatchEx.getName(),
                    expectedTypeName,
                    mismatchEx.getValue());
        };
    }

    private String handleDateMismatch(MethodArgumentTypeMismatchException mismatchEx, Locale locale) {
        Optional<String> expectedDateFormat = getExpectedDateFormat(mismatchEx);
        return expectedDateFormat
                .map(format ->
                        MessageUtils.getMessage(
                                "argument.type.mismatch.with.format",
                                locale,
                                mismatchEx.getName(),
                                format,
                                mismatchEx.getValue()))
                .orElseGet(() ->
                        MessageUtils.getMessage(
                                "argument.type.mismatch.without.format",
                                locale,
                                mismatchEx.getName(),
                                mismatchEx.getValue()));
    }

    private Optional<String> getExpectedDateFormat(MethodArgumentTypeMismatchException ex) {
        try {
            Method method = ex.getParameter().getMethod();

            if (method != null) {
                for (Parameter param : method.getParameters()) {
                    if (param != null && param.isAnnotationPresent(DateTimeFormat.class)) {
                        DateTimeFormat format = param.getAnnotation(DateTimeFormat.class);
                        return Optional.of(format.pattern());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error when trying to recover expected date format. {}", e.getMessage());
        }
        return Optional.empty();
    }
}

