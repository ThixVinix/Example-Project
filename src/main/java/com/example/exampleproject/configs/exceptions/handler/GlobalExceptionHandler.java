package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.ErrorMapResponse;
import com.example.exampleproject.configs.exceptions.ErrorResponse;
import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.configs.exceptions.custom.ResourceNotFoundException;
import com.example.exampleproject.configs.exceptions.handler.helper.ExceptionHandlerHelper;
import com.example.exampleproject.utils.messages.MessageUtils;
import lombok.extern.slf4j.Slf4j;
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

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

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
        Locale locale = ExceptionHandlerHelper.getLocaleFromRequest(request);

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
        Locale locale = ExceptionHandlerHelper.getLocaleFromRequest(request);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ExceptionHandlerHelper.getBadRequestMessage(ex, locale))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class, Throwable.class})
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        Locale locale = ExceptionHandlerHelper.getLocaleFromRequest(request);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage() != null ?
                        ex.getMessage() : MessageUtils.getMessage("unknown.exception.error", locale))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

