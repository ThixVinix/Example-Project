package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.ErrorResponse;
import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.configs.exceptions.custom.DataIntegrityViolationException;
import com.example.exampleproject.configs.exceptions.custom.ResourceNotFoundException;
import com.example.exampleproject.configs.exceptions.custom.UnauthorizedException;
import com.example.exampleproject.configs.exceptions.handler.helper.ExceptionHandlerMessageHelper;
import feign.FeignException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponse(
            responseCode = "404",
            description = "Not Found. The requested resource could not be found on the server. This may happen if " +
                    "the resource does not exist, was removed, or the identifier provided is incorrect. Verify the " +
                    "request URL and parameters.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Resource Not Found Example",
                                            summary = "Example of a 404 error response.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T12:00:00",
                                                          "path": "/api/resource/123",
                                                          "status": 404,
                                                          "error": "Not Found",
                                                          "messages": {
                                                            "message": "Resource with ID '123' was not found."
                                                         }
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    protected ResponseEntity<ErrorResponse> handleResourceNotFoundException(Exception ex,
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

    @ApiResponse(
            responseCode = "405",
            description = "Method Not Allowed. The HTTP method used in the request is not supported by the resource. " +
                    "Ensure that you are using the correct method (e.g., GET, POST, PUT, DELETE) as documented for " +
                    "this API endpoint.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Method Not Allowed Example",
                                            summary = "Example of a 405 error response when using an unsupported " +
                                                    "method.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T12:00:00",
                                                          "path": "/api/resource",
                                                          "status": 405,
                                                          "error": "Method Not Allowed",
                                                          "messages": {
                                                            "message": "POST is not supported for this resource."
                                                          }
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(Exception ex,
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


    @ApiResponse(
            responseCode = "400",
            description = "Bad Request. This error occurs when the server cannot process the request due to invalid " +
                    "syntax, missing required information, or incorrect data formatting. Verify the request " +
                    "parameters, body, and format before retrying.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Bad Request Example",
                                            summary = "Example of a 400 error response due to invalid input.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T14:00:00",
                                                          "path": "/api/resource",
                                                          "status": 400,
                                                          "error": "Bad Request",
                                                          "messages": {
                                                            "field": "Field is required."
                                                          }
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
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
    protected ResponseEntity<ErrorResponse> handleBadRequestException(Exception ex, WebRequest request) {
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

    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized. Authentication is required to access this resource, and the provided " +
                    "credentials are missing, invalid, or expired. Ensure that a valid 'Authorization' header or " +
                    "token is included in the request.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized Example",
                                            summary = "Example of a 401 error response caused by missing or invalid " +
                                                    "authentication credentials.",
                             value = """
                                         {
                                           "timestamp": "2023-01-01T12:00:00",
                                           "path": "/api/resource",
                                           "status": 401,
                                           "error": "Unauthorized",
                                           "messages": {
                                             "message": "Authentication failed due to missing or invalid credentials."
                                           }
                                         }
                                     """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<ErrorResponse> handleUnauthorizedException(Exception ex, WebRequest request) {
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

    @ApiResponse(
            responseCode = "403",
            description = "Forbidden. The server understood the request but is refusing to authorize it. " +
                    "This error occurs when the client does not have the necessary permissions to access the " +
                    "resource. Ensure that the user has the required roles or permissions to perform the requested " +
                    "operation.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Forbidden Example",
                                            summary = "Example of a 403 error response when access is denied.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T15:00:00",
                                                          "path": "/api/protected-resource",
                                                          "status": 403,
                                                          "error": "Forbidden",
                                                          "messages": {
                                                            "message": "Access denied. You do not have sufficient
                                                             permissions to access this resource."
                                                          }
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleForbiddenException(Exception ex, WebRequest request) {
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

    @ApiResponse(
            responseCode = "409",
            description = "Conflict. This occurs when the request cannot be completed due to a conflict in the " +
                    "current state of the resource. This could be caused by duplicate data, resource version " +
                    "conflicts, or business rules violations. Ensure that the data being sent is correct and does " +
                    "not conflict with the current resource state.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Conflict Example",
                                            summary = "Example of a 409 error response caused by a duplicate entry.",
                                      value = """
                                                  {
                                                    "timestamp": "2023-01-01T16:00:00",
                                                    "path": "/api/resource",
                                                    "status": 409,
                                                    "error": "Conflict",
                                                    "messages": {
                                                      "message": "A resource with the identifier '123' already exists."
                                                    }
                                                  }
                                              """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleConflictException(Exception ex,
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

    @ApiResponse(responseCode = "408", description = "Request timed out. This occurs when the server couldn't " +
            "complete the request within the timeout window. This could be due to server overload, a long-running " +
            "operation, or connectivity issues.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Request Timeout Example",
                                            summary = "Example of a 408 error response caused by a timeout.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T17:00:00",
                                                          "path": "/api/resource",
                                                          "status": 408,
                                                          "error": "Request Timeout",
                                                          "messages": {
                                                            "message": "The server timed out waiting for the request
                                                             to complete."
                                                          }
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(TimeoutException.class)
    protected ResponseEntity<ErrorResponse> handleTimeoutException(Exception ex, WebRequest request) {
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

    @ApiResponse(responseCode = "406", description = "Not acceptable. This occurs when the 'Accept' header in the " +
            "request specifies a response format that the server cannot provide. Ensure that the 'Accept' header is " +
            "set to a format supported by the API, such as 'application/json'.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Not Acceptable Example",
                                            summary = "Example of a 406 error response caused by unsupported " +
                                                    "'Accept' header value.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T18:00:00",
                                                          "path": "/api/resource",
                                                          "status": 406,
                                                          "error": "Not Acceptable",
                                                          "messages": {
                                                            "message": "The server cannot produce a response in the
                                                             requested format: 'application/xml'."
                                                          }
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMediaTypeNotAcceptableException(Exception ex,
                                                                                      WebRequest request) {
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


    @ApiResponse(responseCode = "415", description = "Unsupported media type. This occurs when the media type " +
            "provided in the request is not supported by the server. Ensure the 'Content-Type' header and request " +
            "body are formatted correctly according to the API requirements.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Unsupported Media Type Example",
                                            summary = "Example of a 415 error response caused by an unsupported " +
                                                    "'Content-Type' header.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T19:00:00",
                                                          "path": "/api/resource",
                                                          "status": 415,
                                                          "error": "Unsupported Media Type",
                                                          "messages": {
                                                            "message": "The server does not support the media type
                                                             'application/xml' specified in the 'Content-Type' header."
                                                          }
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(Exception ex, WebRequest request) {
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

    @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error. This error occurs when the server encounters an unexpected " +
                    "condition that prevents it from fulfilling the request. Contact the API support team if the " +
                    "issue persists.",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Internal Server Error Example",
                                            summary = "Example of a 500 error response caused by an unexpected " +
                                                    "server-side failure.",
                               value = """
                                           {
                                             "timestamp": "2023-01-01T20:00:00",
                                             "path": "/api/resource",
                                             "status": 500,
                                             "error": "Internal Server Error",
                                             "messages": {
                                               "message": "An unexpected error occurred while processing the request."
                                             }
                                           }
                                       """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler({Exception.class, Throwable.class})
    protected ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
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
    protected ResponseEntity<ErrorResponse> handleFeignClientException(FeignException e, WebRequest request) {
        String requestUri = request.getDescription(false);
        HttpStatus status = HttpStatus.resolve(e.status());
        int statusFeign = e.status();

        logFeignErrorDetails(e, requestUri, status);

        if (statusFeign == -1 || Objects.isNull(status)) {
            return this.handleGlobalException(e, request);
        }

        return getResponseByStatus(status, e, request);
    }

    private void logFeignErrorDetails(FeignException e, String requestUri, HttpStatus status) {
        String responseBody = e.responseBody().map(Object::toString).orElse("No response body");
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
    }

    private ResponseEntity<ErrorResponse> getResponseByStatus(HttpStatus status, FeignException e, WebRequest request) {
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
            default -> this.handleGlobalException(e, request);
        };
    }
}

