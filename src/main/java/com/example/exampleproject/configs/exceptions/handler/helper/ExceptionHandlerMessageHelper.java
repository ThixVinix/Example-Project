package com.example.exampleproject.configs.exceptions.handler.helper;

import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.utils.MessageUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.BindParam;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * <h1>{@link ExceptionHandlerMessageHelper}</h1>
 *
 * <p>The <strong>{@link ExceptionHandlerMessageHelper}</strong> class provides utility methods to generate
 * localized and specific error messages for various exception scenarios commonly encountered
 * in a web application. It acts as a centralized helper to construct meaningful error responses
 * based on the type and details of the exception.</p>
 *
 * <h2>Supported Scenarios</h2>
 * <ul>
 *   <li>Resource or URL didn't find errors.</li>
 *   <li>HTTP method didn't allow errors.</li>
 *   <li>Internal server errors.</li>
 *   <li>Unauthorized access errors.</li>
 *   <li>Forbidden operation errors.</li>
 *   <li>Conflict errors within the application.</li>
 *   <li>Timeout errors.</li>
 *   <li>HTTP media type errors (not acceptable or not supported).</li>
 *   <li>File upload size exceeded errors.</li>
 *   <li>Bad request errors caused by invalid parameters, missing properties, or data violations.</li>
 * </ul>
 *
 * <h2>Features</h2>
 * <p>This class uses exception-specific details to construct precise and context-aware messages,
 * while providing default message options when necessary. Most of the methods are intended for
 * internal use and are designed to generate detailed error responses, helping in debugging and
 * enhancing the user experience.</p>
 *
 * <h2>Implementation</h2>
 * <p>
 * This class is designed as a <strong>utility class</strong>, which means:
 * </p>
 * <ul>
 *   <li>All methods are <code>static</code>.</li>
 *   <li>It includes a private constructor to prevent instantiation.</li>
 * </ul>
 */
@Slf4j
public class ExceptionHandlerMessageHelper {

    private ExceptionHandlerMessageHelper() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    private static final String DEFAULT_MESSAGE_KEY = "message";

    private static final String JSON_MALFORMED_MESSAGE_VALUE = "msg.exception.handler.json.malformed";

    private static final Pattern TYPE_PATTERN_MESSAGE_EXCEPTION =
            Pattern.compile("Cannot deserialize value of type `(.*?)`");

    private static final Pattern MISSING_PROPERTY_PATTERN =
            Pattern.compile("Missing required creator property '(.*?)'");

    /**
     * Generates a specific message indicating that a resource or URL was not found,
     * based on the type of the given exception.
     *
     * @param ex The exception that triggered the not found message.
     *           It can be an instance of {@link NoResourceFoundException} or
     *           {@link NoHandlerFoundException}.
     *           Other exceptions may default to a generic not found message.
     * @return A localized error message indicating the resource or URL was not found.
     */
    public static String getNotFoundMessage(Exception ex) {
        if (ex instanceof NoResourceFoundException || ex instanceof NoHandlerFoundException) {
            return getMessageUtils("msg.exception.handler.resource.url.not.found");
        }

        return getErrorMessage(ex, "msg.exception.handler.resource.not.found");
    }

    /**
     * Generates an error message indicating that the requested HTTP method is not supported.
     *
     * @param ex The exception that triggered the method didn't allow error, expected to be an instance
     *           of {@link HttpRequestMethodNotSupportedException}.
     * @return A string containing the localized error message for the "HTTP Method Not Allowed" error.
     */
    public static String getMethodNotAllowedMessage(Exception ex) {
        HttpRequestMethodNotSupportedException methodNotSupportedEx = (HttpRequestMethodNotSupportedException) ex;
        return getMessageUtils("msg.exception.handler.http.method.not.supported",
                methodNotSupportedEx.getMethod());
    }

    /**
     * Generates a default error message for internal server errors.
     *
     * @param ex The exception that triggered the internal server error. Typically expected to
     *           encapsulate details about the unexpected condition encountered by the server.
     * @return A localized error message indicating an internal server error. If the exception
     * does not contain a message, a default message is returned.
     */
    public static String getInternalServerErrorMessage(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.unknown.error");
    }

    /**
     * Generates a message indicating that the request is unauthorized.
     * The message is derived from the provided exception or a default
     * unauthorized message key.
     *
     * @param ex The exception that triggered the unauthorized message.
     *           It may contain additional details about the authentication failure.
     * @return A localized error message indicating that the request is unauthorized.
     */
    public static String getUnauthorizedMessage(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.unauthorized.default");
    }

    /**
     * Generates an error message indicating that access to a resource or operation is forbidden.
     * The message is provided either from the exception details or a default forbidden message key.
     *
     * @param ex The exception that triggered the forbidden error message. It may
     *           contain specific details about why access is denied.
     * @return A localized error message indicating that the operation or resource access is forbidden.
     */
    public static String getForbiddenMessage(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.access.denied.default");
    }

    /**
     * Generates an error message indicating a conflict error in the application.
     * This method retrieves a localized or default error message based on the provided exception.
     *
     * @param ex The exception that triggered the conflict message. This exception
     *           may provide specifics about the data integrity violation or conflict.
     * @return A string containing the localized conflict error message or a default
     * message if the exception does not contain sufficient information.
     */
    public static String getConflictMessage(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.data.integrity.violation.default");
    }

    /**
     * Generates an error message indicating that a request has timed out.
     * The message is derived from the provided exception or a default timeout message key.
     *
     * @param ex The exception that triggered the timeout message.
     *           This may provide additional context about the timeout condition.
     * @return A localized error message indicating that the request has timed out.
     */
    public static String getTimeoutMessage(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.timeout.default");
    }

    /**
     * Generates an error message indicating that the service is currently unavailable.
     * The message is derived from the provided exception or a default service unavailable message key.
     *
     * @param ex The exception that triggered the service-unavailable message.
     *           This may provide additional context about the unavailability condition.
     * @return A localized error message indicating that the service is currently unavailable.
     */
    public static String getServiceUnavailableMessage(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.service.unavailable.default");
    }

    /**
     * Generates an error message indicating that a bad gateway error has occurred.
     * The message is derived from the provided exception or a default bad gateway message key.
     *
     * @param ex The exception that triggered the bad gateway message.
     *           This may provide additional context about the gateway communication failure.
     * @return A localized error message indicating that a bad gateway error has occurred.
     */
    public static String getBadGatewayMessage(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.bad.gateway.default");
    }

    /**
     * Generates an error message indicating that the requested HTTP media type
     * is not acceptable. This typically happens when the 'Accept' header in
     * the request specifies a response format not supported by the server.
     *
     * @param ex The exception that triggered the "HTTP Media Type Not Acceptable" error.
     *           This is expected to contain details regarding the media type conflict.
     * @return A localized error message indicating that the requested media type
     * is not acceptable.
     */
    public static String getHttpMediaTypeNotAcceptableException(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.media.type.not.acceptable.default");
    }

    /**
     * Generates an error message indicating that the provided HTTP media type is not supported.
     * This error usually occurs when the 'Content-Type' header in the request specifies a media type
     * that the server cannot process.
     *
     * @param ex The exception that triggered the "HTTP Media Type Not Supported" error.
     *           This is expected to contain details about the unsupported media type issue.
     * @return A localized error message indicating that the specified media type is not supported.
     */
    public static String getHttpMediaTypeNotSupportedException(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.media.type.not.supported.default");
    }

    /**
     * Generates an error message indicating that the maximum upload size has been exceeded.
     * The error message is localized and derived either directly from the details
     * encapsulated in the provided exception or defaults to a predefined message key.
     *
     * @param ex The exception that triggered the upload size exceeded the error.
     *           This exception is typically related to file upload size constraints,
     *           such as {@code MaxUploadSizeExceededException} or similar.
     * @return A localized error message indicating that the uploaded file size exceeded
     * the allowed limit. If the exception does not contain specifics, a default
     * message is returned.
     */
    public static String getMaxUploadSizeExceededException(Exception ex) {
        return getErrorMessage(ex, "msg.exception.handler.max.upload.size.exceeded.default");
    }

    /**
     * Generates an appropriate error message based on the provided exception, specifically for
     * bad request scenarios. The error message is determined by the type of the exception passed in.
     *
     * @param ex The exception that triggered the bad request. This could include various
     *           types of exceptions such as {@code MethodArgumentNotValidException},
     *           {@code HttpMessageNotReadableException}, {@code MissingServletRequestParameterException},
     *           {@code MissingRequestHeaderException}, {@code MethodArgumentTypeMismatchException},
     *           {@code ConstraintViolationException}, or {@code HandlerMethodValidationException}.
     *           If the exception is null or an unsupported type, a default error message is generated.
     * @return A map containing keys and their associated error messages derived
     * from the provided exception. Each message explains the specific issue.
     */
    public static Map<String, String> getBadRequestMessage(Exception ex) {
        return switch (ex) {
            case MethodArgumentNotValidException notValidEx -> getMethodArgumentNotValidMessage(notValidEx);
            case HttpMessageNotReadableException notReadableEx -> getNotReadableMessage(notReadableEx);
            case MissingServletRequestParameterException missingEx ->
                    getMissingServletRequestParameterMessage(missingEx);
            case MissingRequestHeaderException missingHeaderEx -> getMissingRequestHeaderMessage(missingHeaderEx);
            case MethodArgumentTypeMismatchException mismatchEx ->
                    ParameterValidationMessageHelper.getMismatchMessage(mismatchEx);
            case ConstraintViolationException constraintEx ->
                    ParameterValidationMessageHelper.getConstraintViolationMessage(constraintEx);
            case HandlerMethodValidationException handlerMethodEx -> getHandlerMethodValidationMessage(handlerMethodEx);
            case null, default -> getDefaultBadRequestMessage(ex);
        };
    }

    private static Map<String, String> getMethodArgumentNotValidMessage(MethodArgumentNotValidException notValidEx) {
        Object target = notValidEx.getTarget();
        Class<?> targetClass = nonNull(target) ? target.getClass() : null;

        return notValidEx.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> getFieldName(notValidEx, error.getField()),
                        error -> getFieldErrorMessage(targetClass, error),
                        ParameterValidationMessageHelper::mergeErrorMessages
                ));
    }

    private static String getFieldName(MethodArgumentNotValidException notValidEx, String originalFieldName) {
        Object target = notValidEx.getTarget();
        return (nonNull(target)) ? resolveJsonPropertyPath(target.getClass(), originalFieldName) : originalFieldName;
    }

    private static String resolveJsonPropertyPath(Class<?> clazz, String fieldPath) {
        if (isNull(fieldPath) || fieldPath.isBlank()) {
            return fieldPath;
        }

        StringJoiner resolvedPath = new StringJoiner(".");
        Class<?> currentClass = clazz;

        for (String segment : splitFieldPath(fieldPath)) {
            String fieldName = getBaseFieldName(segment);
            String indexSuffix = segment.substring(fieldName.length());

            Field field = getFieldRecursive(currentClass, fieldName);
            if (nonNull(field)) {
                String mappedName = getMappedName(currentClass, field);
                resolvedPath.add(mappedName + indexSuffix);
                currentClass = getNextClass(field);
            } else {
                log.debug("Field not found: {} in class: {}", fieldName, currentClass.getName());
                resolvedPath.add(segment);
            }
        }
        return resolvedPath.toString();
    }

    private static String getBaseFieldName(String segment) {
        int bracketIndex = segment.indexOf('[');
        return (bracketIndex == -1) ? segment : segment.substring(0, bracketIndex);
    }

    public static String getMappedName(Class<?> clazz, Field field) {
        // 1. Try JsonProperty on the field
        String jsonPropertyName = getJsonPropertyName(field);
        if (!jsonPropertyName.equals(field.getName())) {
            return jsonPropertyName;
        }

        // 2. Try BindParam on the field
        String bindParamName = getBindParamName(field);
        if (!bindParamName.equals(field.getName())) {
            return bindParamName;
        }

        // 3. Try BindParam on Constructor parameters
        return getConstructorBindParamName(clazz, field.getName()).orElse(field.getName());
    }

    private static String getJsonPropertyName(Field field) {
        return Optional.ofNullable(field.getAnnotation(JsonProperty.class))
                .map(JsonProperty::value)
                .filter(value -> !value.isBlank())
                .map(String::trim)
                .orElse(field.getName());
    }

    private static String getBindParamName(Field field) {
        return Optional.ofNullable(field.getAnnotation(BindParam.class))
                .map(BindParam::value)
                .filter(value -> !value.isBlank())
                .map(String::trim)
                .orElse(field.getName());
    }

    private static Optional<String> getConstructorBindParamName(Class<?> clazz, String fieldName) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            Optional<String> bindParamName = findBindParamName(constructor, fieldName);
            if (bindParamName.isPresent()) {
                return bindParamName;
            }
        }
        return Optional.empty();
    }

    private static Optional<String> findBindParamName(Constructor<?> constructor, String fieldName) {
        for (Parameter parameter : constructor.getParameters()) {
            if (!parameter.getName().equals(fieldName)) {
                continue;
            }

            BindParam bindParam = parameter.getAnnotation(BindParam.class);
            if (isUsableBindParam(bindParam)) {
                return Optional.of(bindParam.value().trim());
            }
        }
        return Optional.empty();
    }

    private static boolean isUsableBindParam(BindParam bindParam) {
        return nonNull(bindParam) && !bindParam.value().isBlank();
    }

    private static List<String> splitFieldPath(String fieldPath) {
        if (isNull(fieldPath) || fieldPath.isEmpty()) {
            return List.of();
        }

        List<String> segments = new ArrayList<>();
        FieldPathParserHelper parser = new FieldPathParserHelper(fieldPath);

        if (!parser.parse(segments)) {
            log.warn("Unbalanced brackets in field path: {}", fieldPath);
            return List.of(fieldPath);
        }

        return segments;
    }

    public static Field getFieldRecursive(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (nonNull(currentClass) && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException _) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    private static Class<?> getNextClass(Field field) {
        Class<?> fieldType = field.getType();
        Class<?> nextClass;

        if (fieldType.isArray()) {
            nextClass = fieldType.getComponentType();
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            nextClass = getGenericTypeArgument(field, 0);
        } else if (Map.class.isAssignableFrom(fieldType)) {
            nextClass = getGenericTypeArgument(field, 1);
        } else {
            nextClass = fieldType;
        }

        return nextClass;
    }

    private static Class<?> getGenericTypeArgument(Field field, int index) {
        if (field.getGenericType() instanceof ParameterizedType pt) {
            Type[] typeArgs = pt.getActualTypeArguments();
            if (typeArgs.length > index && typeArgs[index] instanceof Class<?> genericClass) {
                return genericClass;
            }
        }
        return Object.class;
    }

    private static String getFieldErrorMessage(Class<?> clazz, FieldError error) {
        if ("typeMismatch".equals(error.getCode()) || "methodInvocation".equals(error.getCode())) {
            return getTypeMismatchMessage(clazz, error);
        }

        return nonNull(error.getDefaultMessage())
                ? error.getDefaultMessage()
                : getMessageUtils("msg.exception.handler.argument.type.invalid");
    }

    private static String getTypeMismatchMessage(Class<?> clazz, FieldError error) {
        Field field = getFieldRecursive(clazz, error.getField());

        if (nonNull(field)) {
            return buildTypeMismatchMessage(field, error);
        }

        return getMessageUtils("msg.exception.handler.argument.type.mismatch.without.format", error.getRejectedValue());
    }

    private static String buildTypeMismatchMessage(Field field, FieldError error) {
        String explicitPattern = getExplicitDateTimePattern(field);

        if (nonNull(explicitPattern)) {
            return getMessageUtils(
                    "msg.exception.handler.argument.type.mismatch.with.format",
                    explicitPattern,
                    error.getRejectedValue());
        }

        Class<?> type = field.getType();
        return ParameterValidationMessageHelper.getDefaultDateTimePatternForType(type)
                .map(pattern -> getMessageUtils(
                        "msg.exception.handler.argument.type.mismatch.with.format",
                        pattern,
                        error.getRejectedValue()))
                .orElseGet(() -> getMessageUtils(
                        "msg.exception.handler.argument.type.mismatch.default",
                        type.getSimpleName(),
                        error.getRejectedValue()));
    }

    private static String getExplicitDateTimePattern(Field field) {
        DateTimeFormat dateTimeFormat = field.getAnnotation(DateTimeFormat.class);
        if (isNull(dateTimeFormat)) {
            return null;
        }

        String pattern = dateTimeFormat.pattern().trim();
        return pattern.isEmpty() ? null : pattern;
    }

    private static Map<String, String> getNotReadableMessage(HttpMessageNotReadableException notReadableException) {
        Throwable rootCause = notReadableException.getRootCause();

        if (rootCause instanceof BusinessException businessException) {
            return handleBusinessException(businessException);
        }

        if (rootCause instanceof JsonMappingException jsonMappingException) {
            return handleJsonMappingException(jsonMappingException);
        }

        return Map.of(DEFAULT_MESSAGE_KEY, getMessageUtils(JSON_MALFORMED_MESSAGE_VALUE));
    }

    private static Map<String, String> handleBusinessException(BusinessException businessException) {
        String errorMessage = businessException.getMessage();
        if (nonNull(errorMessage)) {

            if (nonNull(businessException.getFieldName()) && !businessException.getFieldName().trim().isEmpty()) {
                return Map.of(businessException.getFieldName(), errorMessage);
            }

            return Map.of(DEFAULT_MESSAGE_KEY, errorMessage);
        }
        return Map.of(DEFAULT_MESSAGE_KEY, getMessageUtils(JSON_MALFORMED_MESSAGE_VALUE));
    }

    private static Map<String, String> handleJsonMappingException(JsonMappingException jsonMappingException) {
        Optional<String> missingPropertyOptional = extractMissingProperty(jsonMappingException.getOriginalMessage());
        if (missingPropertyOptional.isPresent()) {
            String missingField = missingPropertyOptional.get();
            return Map.of(missingField, getMessageUtils("msg.exception.handler.missing.parameter"));
        }

        String fieldName = jsonMappingException.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining("."));

        return extractTargetType(jsonMappingException)
                .map(targetType -> Map.of(fieldName,
                        getMessageUtils("msg.exception.handler.invalid.deserialize", targetType)))
                .orElseGet(() -> Map.of(DEFAULT_MESSAGE_KEY, getMessageUtils(JSON_MALFORMED_MESSAGE_VALUE)));
    }


    private static Optional<String> extractTargetType(JsonMappingException jsonMappingException) {
        Matcher matcher = TYPE_PATTERN_MESSAGE_EXCEPTION.matcher(jsonMappingException.getOriginalMessage());
        return matcher.find() ? extractSimpleName(matcher.group(1)) : Optional.empty();
    }

    private static Optional<String> extractMissingProperty(String originalMessage) {
        Matcher matcher = MISSING_PROPERTY_PATTERN.matcher(originalMessage);
        return matcher.find() ? Optional.ofNullable(matcher.group(1)) : Optional.empty();
    }


    private static Optional<String> extractSimpleName(String fullName) {
        if (Objects.isNull(fullName) || fullName.trim().isEmpty()) {
            return Optional.empty();
        }
        String simpleName = fullName.substring(fullName.lastIndexOf('.') + 1);
        return Optional.of(simpleName);
    }

    private static Map<String, String> getMissingServletRequestParameterMessage(
            MissingServletRequestParameterException missingEx) {
        return Map.of(missingEx.getParameterName(), getMessageUtils("msg.exception.handler.missing.parameter"));
    }

    private static Map<String, String> getMissingRequestHeaderMessage(MissingRequestHeaderException missingEx) {
        return Map.of(missingEx.getHeaderName(), getMessageUtils("msg.exception.handler.missing.header"));
    }

    private static Map<String, String> getHandlerMethodValidationMessage(HandlerMethodValidationException ex) {
        log.error(ex.getMessage(), ex);

        log.warn("Please check if the controller class is using the \"@Validated\" annotation to improve the mapping " +
                "of errors with custom messages, and make sure the \"@Valid\" annotation is present when using " +
                "\"@RequestBody\" or \"@RequestPart\".");

        return Map.of(DEFAULT_MESSAGE_KEY, getMessageUtils("msg.exception.handler.validation.failure"));
    }

    private static Map<String, String> getDefaultBadRequestMessage(Exception ex) {
        String message;

        if (ex instanceof WebClientResponseException webClientEx) {
            Optional<String> extractedMessageOptional =
                    WebClientMessageExtractorHelper.extractMessageFromWebClientResponseException(webClientEx);
            String details = extractedMessageOptional
                    .orElseGet(() -> getMessageUtils("msg.exception.handler.unknown.bad.request.error"));
            message = getMessageUtils("msg.exception.handler.web.client.error", details);
            return Map.of(DEFAULT_MESSAGE_KEY, message);
        }

        if (nonNull(ex) && nonNull(ex.getMessage())) {
            message = ex.getMessage();
        } else {
            message = getMessageUtils("msg.exception.handler.unknown.bad.request.error");
        }
        return Map.of(DEFAULT_MESSAGE_KEY, message);
    }

    private static String getErrorMessage(Exception ex, String defaultMessageValue) {
        String message;

        if (ex instanceof WebClientResponseException webClientEx) {
            Optional<String> extractedMessageOptional =
                    WebClientMessageExtractorHelper.extractMessageFromWebClientResponseException(webClientEx);
            String details = extractedMessageOptional.orElseGet(() -> getMessageUtils(defaultMessageValue));
            message = getMessageUtils("msg.exception.handler.web.client.error", details);
            return message;
        }

        if (nonNull(ex) && nonNull(ex.getMessage())) {
            message = ex.getMessage();
        } else {
            message = getMessageUtils(defaultMessageValue);
        }
        return message;
    }

    private static String getMessageUtils(String messageKey, Object... params) {
        return MessageUtils.getMessage(messageKey, params);
    }

}
