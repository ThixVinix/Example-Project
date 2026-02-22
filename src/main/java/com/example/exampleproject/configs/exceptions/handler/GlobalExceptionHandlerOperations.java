package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.BaseError;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;
import java.util.function.Function;

/**
 * Interface defining operations that can be delegated from GlobalExceptionHandler.
 * This interface allows the WebClientExceptionHandlerDelegate to call back
 * to the main exception handler without tight coupling.
 */
public interface GlobalExceptionHandlerOperations {

    /**
     * Handles a global exception scenario.
     *
     * @param ex      the exception
     * @param request the web request
     * @return a ResponseEntity with ErrorSingleResponse
     */
    ResponseEntity<ErrorSingleResponse> handleGlobalException(Exception ex, WebRequest request);

    /**
     * Handles a bad request exception scenario.
     *
     * @param ex      the exception
     * @param request the web request
     * @return a ResponseEntity with BaseError (can be single or multiple)
     */
    @SuppressWarnings("squid:S1452")
    ResponseEntity<? extends BaseError> handleBadRequestException(Exception ex, WebRequest request);

    /**
     * Handles a single error response scenario.
     *
     * @param ex              the exception
     * @param request         the web request
     * @param status          the HTTP status
     * @param logMessage      the log message template
     * @param messageSupplier the function to supply the error message
     * @return a ResponseEntity with ErrorSingleResponse
     */
    ResponseEntity<ErrorSingleResponse> handleSingleErrorResponse(
            Exception ex,
            WebRequest request,
            HttpStatus status,
            String logMessage,
            Function<Exception, String> messageSupplier);

    /**
     * Handles a multiple error response scenario.
     *
     * @param ex               the exception
     * @param request          the web request
     * @param messagesSupplier the function to supply the error messages map
     * @return a ResponseEntity with BaseError (can be single or multiple)
     */
    @SuppressWarnings("squid:S1452")
    ResponseEntity<? extends BaseError> handleMultipleErrorResponse(
            Exception ex,
            WebRequest request,
            Function<Exception, Map<String, String>> messagesSupplier);
}
