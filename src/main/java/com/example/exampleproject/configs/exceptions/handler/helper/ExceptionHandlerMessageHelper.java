package com.example.exampleproject.configs.exceptions.handler.helper;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.utils.MessageUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ExceptionHandlerMessageHelper {

    private ExceptionHandlerMessageHelper() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    private static final String MESSAGE_KEY = "message";

    private static final String LOCAL_DATE_TYPE = "LocalDate";
    private static final String LOCAL_DATE_TIME_TYPE = "LocalDateTime";
    private static final String ZONED_DATE_TIME_TYPE = "ZonedDateTime";
    private static final String LOCAL_TIME_TYPE = "LocalTime";
    private static final String DATE_TYPE = "Date";

    /**
     * Generates a not found message based on the provided exception.
     *
     * @param ex the exception that occurred
     * @return a map containing a not found message
     */
    public static Map<String, String> getNotFoundMessage(Exception ex) {
        if (ex instanceof NoResourceFoundException) {
            return Map.of(MESSAGE_KEY, MessageUtils.getMessage("msg.exception.handler.resource.url.not.found"));
        }

        return Map.of(MESSAGE_KEY, MessageUtils.getMessage("msg.exception.handler.resource.not.found"));
    }

    /**
     * Generates a method not allowed message based on the given exception.
     *
     * @param ex the HttpRequestMethodNotSupportedException containing the unsupported method information
     * @return a map containing the method not allowed message
     */
    public static Map<String, String> getMethodNotAllowedMessage(HttpRequestMethodNotSupportedException ex) {
        return Map.of(MESSAGE_KEY,
                MessageUtils.getMessage("msg.exception.handler.http.method.not.supported", ex.getMethod()));
    }

    /**
     * Generates an internal server error message based on the provided exception.
     *
     * @param ex the exception from which the error message will be generated
     * @return a map containing the error message with a specific key
     */
    public static Map<String, String> getInternalServerErrorMessage(Exception ex) {
        return Map.of(MESSAGE_KEY,
                ex.getMessage() != null ?
                        ex.getMessage() :
                        MessageUtils.getMessage("msg.exception.handler.unknown.exception.error"));
    }

    /**
     * Constructs a detailed error message based on the type of the provided exception.
     *
     * @param ex The exception that caused the bad request.
     * @return A map containing specific details about the bad request derived from the exception.
     */
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
            case MissingRequestHeaderException missingRequestHeaderException -> {
                return getMissingRequestHeaderMessage(missingRequestHeaderException);
            }
            case MethodArgumentTypeMismatchException mismatchEx -> {
                return getMismatchMessage(mismatchEx);
            }
            case ConstraintViolationException constraintViolationEx -> {
                return getConstraintViolationMessage(constraintViolationEx);
            }
            case HandlerMethodValidationException handlerMethodEx -> {
                return getHandlerMethodValidationMessage(handlerMethodEx);
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
                        error -> getFieldName(notValidEx, error.getField()),
                        ExceptionHandlerMessageHelper::getErrorMessage,
                        ExceptionHandlerMessageHelper::mergeErrorMessages
                ));
    }

    private static String getFieldName(MethodArgumentNotValidException notValidEx, String originalFieldName) {
        try {
            Object target = notValidEx.getTarget();
            if (target != null) {
                Field field = target.getClass().getDeclaredField(originalFieldName);
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && !jsonProperty.value().trim().isEmpty()) {
                    return jsonProperty.value().trim();
                }
            }
        } catch (NoSuchFieldException | SecurityException ex) {
            log.warn(ex.getMessage(), ex);
        }
        return originalFieldName;
    }

    private static String getErrorMessage(FieldError error) {
        return error.getDefaultMessage() != null
                ? error.getDefaultMessage()
                : MessageUtils.getMessage("msg.exception.handler.argument.type.invalid");
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

    private static Map<String, String> getMissingRequestHeaderMessage(MissingRequestHeaderException missingEx) {
        return Map.of(MESSAGE_KEY,
                MessageUtils.getMessage("msg.exception.handler.missing.header", missingEx.getHeaderName()));
    }

    private static Map<String, String> getMismatchMessage(MethodArgumentTypeMismatchException mismatchEx) {

        Optional<Parameter> parameter = searchParameter(mismatchEx);

        String expectedTypeName =
                Optional.ofNullable(mismatchEx.getRequiredType())
                        .map(Class::getSimpleName)
                        .orElse(null);

        return switch (expectedTypeName) {
            case null -> Map.of(mismatchEx.getName(),
                    MessageUtils.getMessage(
                            "msg.exception.handler.argument.type.mismatch.without.format",
                            mismatchEx.getValue()));
            case LOCAL_DATE_TYPE,
                 LOCAL_DATE_TIME_TYPE,
                 ZONED_DATE_TIME_TYPE,
                 LOCAL_TIME_TYPE,
                 DATE_TYPE -> getDateTimeMismatchMessage(
                    mismatchEx, expectedTypeName, parameter.orElse(null));
            default -> getDefaultMismatchMessage(mismatchEx, expectedTypeName);
        };
    }

    private static Optional<Parameter> searchParameter(
            MethodArgumentTypeMismatchException mismatchEx) {
        try {
            Method method = mismatchEx.getParameter().getMethod();
            if (method != null) {
                for (Parameter param : method.getParameters()) {
                    if (containsRequestTypeAnnotation(mismatchEx, param)) {
                        return Optional.of(param);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error when trying to recover request type. {}", e.getMessage());
        }
        return Optional.empty();
    }

    private static Map<String, String> getDateTimeMismatchMessage(MethodArgumentTypeMismatchException mismatchEx,
                                                                  String expectTypeName,
                                                                  Parameter parameter) {
        if (Objects.nonNull(parameter)) {
            return extractDateTimeFormatPatternMessage(mismatchEx, parameter);
        }

        return getDefaultMismatchMessage(mismatchEx, expectTypeName);
    }

    private static Map<String, String> getDefaultMismatchMessage(MethodArgumentTypeMismatchException mismatchEx,
                                                                 String expectedTypeName) {
        return Map.of(mismatchEx.getName(), MessageUtils.getMessage(
                "msg.exception.handler.argument.type.mismatch.default",
                expectedTypeName,
                mismatchEx.getValue()));
    }

    private static boolean containsRequestTypeAnnotation(MethodArgumentTypeMismatchException ex, Parameter param) {
        return getAnnotationValue(param, RequestParam.class)
                .map(value -> value.equals(ex.getName())).orElse(false)
                || getAnnotationValue(param, RequestHeader.class)
                .map(value -> value.equals(ex.getName())).orElse(false)
                || getAnnotationValue(param, PathVariable.class)
                .map(value -> value.equals(ex.getName())).orElse(false);
    }

    private static Map<String, String> extractDateTimeFormatPatternMessage(MethodArgumentTypeMismatchException ex,
                                                                           Parameter param) {
        String paramName = getParamName(param);

        if (param.isAnnotationPresent(DateTimeFormat.class)) {
            DateTimeFormat dateTimeFormat = param.getAnnotation(DateTimeFormat.class);

            if (!dateTimeFormat.pattern().trim().isEmpty()) {
                return Map.of(paramName, MessageUtils.getMessage(
                        "msg.exception.handler.argument.type.mismatch.with.format",
                        dateTimeFormat.pattern().trim(),
                        ex.getValue()));
            }
        }

        Optional<String> defaultDateTimePatternOptional = getDefaultDateTimePatternForType(param.getType());

        return defaultDateTimePatternOptional
                .map(s -> Map.of(paramName, MessageUtils.getMessage(
                        "msg.exception.handler.argument.type.mismatch.with.format", s, ex.getValue())))
                .orElseGet(() -> Map.of(paramName, MessageUtils.getMessage(
                        "msg.exception.handler.argument.type.mismatch.without.format", ex.getValue())));
    }

    private static Optional<String> getDefaultDateTimePatternForType(Class<?> type) {
        return switch (type.getSimpleName()) {
            case LOCAL_DATE_TYPE -> Optional.of("yyyy-MM-dd");
            case LOCAL_DATE_TIME_TYPE -> Optional.of("yyyy-MM-dd'T'HH:mm:ss");
            case ZONED_DATE_TIME_TYPE -> Optional.of("yyyy-MM-dd'T'HH:mm:ss.SSSXXX'Z'");
            case LOCAL_TIME_TYPE -> Optional.of("HH:mm:ss");
            default -> Optional.empty();
        };
    }

    private static String getParamName(Parameter param) {
        return getAnnotationValue(param, RequestParam.class)
                .or(() -> getAnnotationValue(param, RequestHeader.class))
                .or(() -> getAnnotationValue(param, PathVariable.class))
                .orElse(param.getName());
    }

    public static Map<String, String> getConstraintViolationMessage(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldName = getParameterName(violation);
            String errorMessage = violation.getMessage();
            if (errors.containsKey(fieldName)) {
                errorMessage = mergeErrorMessages(errors.get(fieldName), errorMessage);
            }
            errors.put(fieldName, errorMessage);
        }

        return errors;
    }

    private static String getParameterName(ConstraintViolation<?> violation) {
        try {
            Object rootBean = violation.getRootBean();
            String[] propertyPathSegments = violation.getPropertyPath().toString().split("\\.");
            String rootMethodName = propertyPathSegments[0];
            Method matchedMethod = findMethodByName(rootBean, rootMethodName);
            if (matchedMethod != null) {
                for (Parameter parameter : matchedMethod.getParameters()) {
                    if (violation.getPropertyPath().toString().contains(parameter.getName())) {
                        return getParamName(parameter);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        String[] fieldParts = violation.getPropertyPath().toString().split("\\.");
        return fieldParts[fieldParts.length - 1];
    }

    private static Method findMethodByName(Object rootBean, String methodName) {
        for (Method method : rootBean.getClass().getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    private static String mergeErrorMessages(String existingValue, String newValue) {
        if (existingValue.endsWith(".")) {
            existingValue = existingValue.substring(0, existingValue.length() - 1) + "; " + newValue;
        } else {
            existingValue = existingValue + "; " + newValue;
        }
        return existingValue;
    }

    private static Optional<String> getAnnotationValue(Parameter param, Class<? extends Annotation> annotationClass) {
        Annotation annotation = param.getAnnotation(annotationClass);
        if (annotation instanceof RequestParam requestParam) {
            return Optional.of(requestParam.value().trim().isEmpty() ? param.getName() : requestParam.value().trim());
        } else if (annotation instanceof RequestHeader requestHeader) {
            return Optional.of(requestHeader.value().trim().isEmpty() ? param.getName() : requestHeader.value().trim());
        } else if (annotation instanceof PathVariable requestPath) {
            return Optional.of(requestPath.value().trim().isEmpty() ? param.getName() : requestPath.value().trim());
        }
        return Optional.empty();
    }

    private static Map<String, String> getHandlerMethodValidationMessage(HandlerMethodValidationException ex) {
        log.error(ex.getMessage(), ex);

        log.warn("Please check if the controller class is using the \"@Validated\" annotation to improve the mapping " +
                "of errors with custom messages.");
        return Map.of(MESSAGE_KEY, MessageUtils.getMessage("msg.exception.handler.validation.failure"));
    }

    private static Map<String, String> getDefaultBadRequestMessage(Exception ex) {
        if (ex != null && ex.getMessage() != null) {
            return Map.of(MESSAGE_KEY, ex.getMessage());
        } else {
            return Map.of(MESSAGE_KEY, MessageUtils.getMessage("msg.exception.handler.unknown.bad.request.error"));
        }
    }

}
