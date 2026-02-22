package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.BaseError;
import com.example.exampleproject.configs.exceptions.ErrorMultipleResponse;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Factory class responsible for creating error response objects.
 * This class centralizes the creation logic for different types of error responses.
 */
public class ErrorResponseFactory {

    private ErrorResponseFactory() {
        throw new IllegalStateException("Factory class cannot be instantiated");
    }

    /**
     * Creates an appropriate error response based on the number of error messages.
     *
     * @param messages the map of error messages
     * @param path     the request path
     * @return a BaseError instance (either ErrorSingleResponse or ErrorMultipleResponse)
     */
    public static BaseError createAppropriateErrorResponse(Map<String, String> messages, String path) {
        if (messages.size() == NumberUtils.INTEGER_ONE && messages.containsKey("message")) {
            return createErrorSingleResponse(
                    HttpStatus.BAD_REQUEST,
                    messages.get("message"),
                    path
            );
        }

        return createErrorMultipleResponse(messages, path);
    }

    /**
     * Creates a single error response.
     *
     * @param status  the HTTP status
     * @param message the error message
     * @param path    the request path
     * @return an ErrorSingleResponse instance
     */
    public static ErrorSingleResponse createErrorSingleResponse(HttpStatus status, String message, String path) {
        return ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }

    /**
     * Creates a multiple error response.
     *
     * @param messages the map of error messages
     * @param path     the request path
     * @return an ErrorMultipleResponse instance
     */
    public static ErrorMultipleResponse createErrorMultipleResponse(Map<String, String> messages, String path) {
        return ErrorMultipleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .messages(messages)
                .path(path)
                .build();
    }
}
