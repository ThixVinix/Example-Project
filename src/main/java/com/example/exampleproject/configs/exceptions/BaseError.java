package com.example.exampleproject.configs.exceptions;

import com.example.exampleproject.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Base interface for defining a structured error response format in the application.
 * This interface ensures that all error response implementations follow a consistent set of attributes. It provides
 * key details about an error, such as the HTTP status code, a short error description, the request path, and the
 * timestamp of the occurrence.
 * <p>
 * Implementations of this interface include:
 * <ul>
 *   <li><strong>{@link ErrorSingleResponse}:</strong> Used for reporting details of a single error encountered
 *   during API processing. It includes a single descriptive message providing additional context about the error.</li>
 *   <li><strong>{@link ErrorMultipleResponse}:</strong> Used for reporting multiple validation errors in a single
 *   response. Errors are presented in a structured map where specific fields are associated with error messages.</li>
 * </ul>
 * <p>
 * This interface facilitates consistent error reporting across the application, ensuring that clients receive
 * structured and predictable error responses.
 */
public interface BaseError {

    @Schema(description = "HTTP status code returned by the API.", example = "400")
    int status();

    @Schema(description = "Descriptive error message associated with the occurred issue.", example = "Bad Request")
    String error();

    @Schema(description = "Request path (endpoint) that triggered the error.", example = "/api/v1/resource")
    String path();

    @Schema(description = "Date and time when the error occurred. Format: " +
            DateUtils.LOCAL_DATE_TIME_DESERIALIZER_FORMAT, example = "2023-10-05 14:48:00")
    LocalDateTime timestamp();
}
