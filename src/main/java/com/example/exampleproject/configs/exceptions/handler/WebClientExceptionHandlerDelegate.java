package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.BaseError;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
import com.example.exampleproject.configs.exceptions.handler.helper.ExceptionHandlerMessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Delegate a class responsible for handling WebClient-specific exceptions.
 * This class centralizes the logic for processing WebClientResponseException
 * and routing to appropriate handlers based on HTTP status codes.
 */
@Slf4j
public class WebClientExceptionHandlerDelegate {

    private final Map<HttpStatus, BiFunction<WebClientResponseException, WebRequest,
            ResponseEntity<? extends BaseError>>> statusHandlers;

    private final GlobalExceptionHandlerOperations operations;

    public WebClientExceptionHandlerDelegate(GlobalExceptionHandlerOperations operations) {
        this.operations = operations;
        this.statusHandlers = new EnumMap<>(HttpStatus.class);
        initializeStatusHandlers();
    }

    private void initializeStatusHandlers() {
        statusHandlers.put(HttpStatus.BAD_REQUEST, this::handleBadRequest);
        statusHandlers.put(HttpStatus.UNAUTHORIZED, this::handleUnauthorized);
        statusHandlers.put(HttpStatus.FORBIDDEN, this::handleForbidden);
        statusHandlers.put(HttpStatus.NOT_FOUND, this::handleNotFound);
        statusHandlers.put(HttpStatus.METHOD_NOT_ALLOWED, this::handleMethodNotAllowed);
        statusHandlers.put(HttpStatus.NOT_ACCEPTABLE, this::handleNotAcceptable);
        statusHandlers.put(HttpStatus.REQUEST_TIMEOUT, this::handleTimeout);
        statusHandlers.put(HttpStatus.CONFLICT, this::handleConflict);
        statusHandlers.put(HttpStatus.UNSUPPORTED_MEDIA_TYPE, this::handleUnsupportedMediaType);
        statusHandlers.put(HttpStatus.PAYLOAD_TOO_LARGE, this::handlePayloadTooLarge);
        statusHandlers.put(HttpStatus.BAD_GATEWAY, this::handleBadGateway);
        statusHandlers.put(HttpStatus.SERVICE_UNAVAILABLE, this::handleServiceUnavailable);
    }

    @SuppressWarnings("squid:S1452")
    public ResponseEntity<? extends BaseError> handleWebClientException(
            WebClientResponseException ex, WebRequest request) {
        String requestUri = request.getDescription(false);
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());

        logWebClientErrorDetails(ex, requestUri, status);

        if (isNull(status)) {
            return operations.handleGlobalException(ex, request);
        }

        return getResponseByStatus(status, ex, request);
    }

    private void logWebClientErrorDetails(WebClientResponseException ex, String requestUri, HttpStatus status) {
        String responseBody = ex.getResponseBodyAsString();
        HttpHeaders responseHeaders = ex.getHeaders();

        log.error("""
                        WEBCLIENT ERROR:
                        URI: {}
                        Status: {}
                        Response Headers: {}
                        Response Body: {}
                        """,
                requestUri,
                nonNull(status) ? status.name() : "Unknown Status",
                responseHeaders,
                responseBody);
    }

    private ResponseEntity<? extends BaseError> getResponseByStatus(
            HttpStatus status, WebClientResponseException ex, WebRequest request) {
        BiFunction<WebClientResponseException, WebRequest, ResponseEntity<? extends BaseError>> handler =
                statusHandlers.get(status);

        if (nonNull(handler)) {
            return handler.apply(ex, request);
        }

        return operations.handleGlobalException(ex, request);
    }

    private ResponseEntity<? extends BaseError> handleBadRequest(WebClientResponseException ex, WebRequest request) {
        return operations.handleBadRequestException(ex, request);
    }

    private ResponseEntity<ErrorSingleResponse> handleUnauthorized(WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.UNAUTHORIZED,
                "Unauthorized: {}",
                ExceptionHandlerMessageHelper::getUnauthorizedMessage);
    }

    private ResponseEntity<ErrorSingleResponse> handleForbidden(WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.FORBIDDEN,
                "Access denied: {}",
                ExceptionHandlerMessageHelper::getForbiddenMessage);
    }

    private ResponseEntity<ErrorSingleResponse> handleNotFound(WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.NOT_FOUND,
                "Resource not found: {}",
                ExceptionHandlerMessageHelper::getNotFoundMessage);
    }

    private ResponseEntity<ErrorSingleResponse> handleMethodNotAllowed(
            WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.METHOD_NOT_ALLOWED,
                "Method not allowed: {}",
                ExceptionHandlerMessageHelper::getMethodNotAllowedMessage);
    }

    private ResponseEntity<ErrorSingleResponse> handleNotAcceptable(WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.NOT_ACCEPTABLE,
                "Not acceptable: {}",
                ExceptionHandlerMessageHelper::getHttpMediaTypeNotAcceptableException);
    }

    private ResponseEntity<ErrorSingleResponse> handleTimeout(WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.REQUEST_TIMEOUT,
                "Request timed out: {}",
                ExceptionHandlerMessageHelper::getTimeoutMessage);
    }

    private ResponseEntity<ErrorSingleResponse> handleConflict(WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.CONFLICT,
                "Conflict: {}",
                ExceptionHandlerMessageHelper::getConflictMessage);
    }

    private ResponseEntity<ErrorSingleResponse> handleUnsupportedMediaType(
            WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported media type: {}",
                ExceptionHandlerMessageHelper::getHttpMediaTypeNotSupportedException);
    }

    private ResponseEntity<ErrorSingleResponse> handlePayloadTooLarge(
            WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.PAYLOAD_TOO_LARGE,
                "Payload too large: {}",
                ExceptionHandlerMessageHelper::getMaxUploadSizeExceededException);
    }

    private ResponseEntity<ErrorSingleResponse> handleBadGateway(WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.BAD_GATEWAY,
                "Bad gateway: {}",
                ExceptionHandlerMessageHelper::getBadGatewayMessage);
    }

    private ResponseEntity<ErrorSingleResponse> handleServiceUnavailable(
            WebClientResponseException ex, WebRequest request) {
        return operations.handleSingleErrorResponse(
                ex, request, HttpStatus.SERVICE_UNAVAILABLE,
                "Service unavailable: {}",
                ExceptionHandlerMessageHelper::getServiceUnavailableMessage);
    }
}
