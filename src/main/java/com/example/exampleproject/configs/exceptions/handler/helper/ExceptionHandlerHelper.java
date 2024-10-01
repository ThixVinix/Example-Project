package com.example.exampleproject.configs.exceptions.handler.helper;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.utils.messages.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

@Slf4j
public class ExceptionHandlerHelper {

    public static String getNotFoundMessage(Exception ex) {
        if (ex instanceof NoResourceFoundException) {
            return MessageUtils.getMessage("resource.url.not.found");
        }

        return MessageUtils.getMessage("resource.not.found");
    }

    public static String getBadRequestMessage(Exception ex) {
        switch (ex) {
            case HttpMessageNotReadableException httpEx -> {
                if (httpEx.getRootCause() instanceof BusinessException
                        && httpEx.getRootCause().getMessage() != null) {
                    return httpEx.getRootCause().getMessage();
                }
                return MessageUtils.getMessage("json.malformed");
            }
            case MissingServletRequestParameterException missingEx -> {
                return MessageUtils.getMessage("missing.parameter", missingEx.getParameterName());
            }
            case MethodArgumentTypeMismatchException mismatchEx -> {
                return getMismatchMessage(mismatchEx);
            }
            case null, default -> {
                if (ex != null && ex.getMessage() != null) {
                    return ex.getMessage();
                } else {
                    return MessageUtils.getMessage("unknown.bad.request.error");
                }
            }
        }
    }

    public static String getMismatchMessage(MethodArgumentTypeMismatchException mismatchEx) {
        String expectedTypeName = (mismatchEx.getRequiredType() != null) ?
                mismatchEx.getRequiredType().getSimpleName() : null;

        return switch (expectedTypeName) {
            case null -> MessageUtils.getMessage(
                    "argument.type.mismatch.without.format",
                    mismatchEx.getName(),
                    mismatchEx.getValue());
            case "LocalDate", "LocalDateTime", "Date", "ZonedDateTime" -> handleDateMismatch(mismatchEx);
            default -> MessageUtils.getMessage(
                    "argument.type.mismatch.default",
                    mismatchEx.getName(),
                    expectedTypeName,
                    mismatchEx.getValue());
        };
    }

    private static String handleDateMismatch(MethodArgumentTypeMismatchException mismatchEx) {
        Optional<String> expectedDateFormat = getExpectedDateFormat(mismatchEx);
        return expectedDateFormat
                .map(format ->
                        MessageUtils.getMessage(
                                "argument.type.mismatch.with.format",
                                mismatchEx.getName(),
                                format,
                                mismatchEx.getValue()))
                .orElseGet(() ->
                        MessageUtils.getMessage(
                                "argument.type.mismatch.without.format",
                                mismatchEx.getName(),
                                mismatchEx.getValue()));
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

}
