package com.example.exampleproject.configs.exceptions.handler.helper;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.utils.messages.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ExceptionHandlerMessageHelper {

    private ExceptionHandlerMessageHelper() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    private static final String MESSAGE_KEY = "message";

    public static Map<String, String> getNotFoundMessage(Exception ex) {
        if (ex instanceof NoResourceFoundException) {
            return Map.of(MESSAGE_KEY, MessageUtils.getMessage("msg.exception.handler.resource.url.not.found"));
        }

        return Map.of(MESSAGE_KEY, MessageUtils.getMessage("msg.exception.handler.resource.not.found"));
    }

    public static Map<String, String> getMethodNotAllowedMessage(HttpRequestMethodNotSupportedException ex) {
        return Map.of(MESSAGE_KEY,
                MessageUtils.getMessage("msg.exception.handler.http.method.not.supported", ex.getMethod()));
    }

    public static Map<String, String> getInternalServerErrorMessage(Exception ex) {
        return Map.of(MESSAGE_KEY,
                ex.getMessage() != null ?
                        ex.getMessage() :
                        MessageUtils.getMessage("msg.exception.handler.unknown.exception.error"));
    }

    public static Map<String, String> getBadRequestMessage(Exception ex) {
        switch (ex) {
            case MethodArgumentNotValidException notValidEx -> {
                return getMethodArgumentNotValidMessage(notValidEx);
            }
            case HttpMessageNotReadableException notReadableEx -> {
                return getNotReadableMessage(notReadableEx);
            }
            case MissingServletRequestParameterException missingEx -> {
                return getMissingServletRequestParameterMessage(missingEx);
            }
            case MethodArgumentTypeMismatchException mismatchEx -> {
                return getMismatchMessage(mismatchEx);
            }
            case null, default -> {
                return getDefaultBadRequestMessage(ex);
            }
        }
    }

    private static Map<String, String> getMethodArgumentNotValidMessage(MethodArgumentNotValidException notValidEx) {
        return notValidEx.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ?
                                error.getDefaultMessage() :
                                MessageUtils.getMessage("msg.exception.handler.argument.type.invalid"),
                        (existingValue, newValue) -> {
                            if (existingValue.endsWith(".")) {
                                existingValue =
                                        existingValue.substring(0, existingValue.length() - 1) + "; "
                                                + newValue;
                            } else {
                                existingValue = existingValue + "; " + newValue;
                            }
                            return existingValue;
                        }
                ));
    }

    private static Map<String, String> getNotReadableMessage(HttpMessageNotReadableException httpEx) {
        Throwable rootCause = httpEx.getRootCause();
        if (rootCause != null && rootCause.getMessage() != null && rootCause instanceof BusinessException) {
            return Map.of(MESSAGE_KEY, rootCause.getMessage());
        }
        return Map.of(MESSAGE_KEY, MessageUtils.getMessage("msg.exception.handler.json.malformed"));
    }

    private static Map<String, String> getMissingServletRequestParameterMessage(
            MissingServletRequestParameterException missingEx) {
        return Map.of(MESSAGE_KEY,
                MessageUtils.getMessage("msg.exception.handler.missing.parameter", missingEx.getParameterName()));
    }

    private static Map<String, String> getMismatchMessage(MethodArgumentTypeMismatchException mismatchEx) {
        String expectedTypeName =
                Optional.ofNullable(mismatchEx.getRequiredType())
                        .map(Class::getSimpleName)
                        .orElse(null);

        return switch (expectedTypeName) {
            case null -> Map.of(MESSAGE_KEY, MessageUtils.getMessage(
                    "msg.exception.handler.argument.type.mismatch.without.format",
                    mismatchEx.getName(),
                    mismatchEx.getValue()));
            case "LocalDate", "LocalDateTime", "Date", "ZonedDateTime" -> handleDateMismatch(mismatchEx);
            default -> Map.of(MESSAGE_KEY, MessageUtils.getMessage(
                    "msg.exception.handler.argument.type.mismatch.default",
                    mismatchEx.getName(),
                    expectedTypeName,
                    mismatchEx.getValue()));
        };
    }

    private static Map<String, String> handleDateMismatch(MethodArgumentTypeMismatchException mismatchEx) {
        Optional<String> expectedDateFormat = getExpectedDateFormat(mismatchEx);
        return expectedDateFormat
                .map(format ->
                        Map.of(MESSAGE_KEY, MessageUtils.getMessage(
                                "msg.exception.handler.argument.type.mismatch.with.format",
                                mismatchEx.getName(),
                                format,
                                mismatchEx.getValue())))
                .orElseGet(() ->
                        Map.of(MESSAGE_KEY, MessageUtils.getMessage(
                                "msg.exception.handler.argument.type.mismatch.without.format",
                                mismatchEx.getName(),
                                mismatchEx.getValue())));
    }

    private static Optional<String> getExpectedDateFormat(MethodArgumentTypeMismatchException ex) {
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

    private static Map<String, String> getDefaultBadRequestMessage(Exception ex) {
        if (ex != null && ex.getMessage() != null) {
            return Map.of(MESSAGE_KEY, ex.getMessage());
        } else {
            return Map.of(MESSAGE_KEY, MessageUtils.getMessage("msg.exception.handler.unknown.bad.request.error"));
        }
    }

}
