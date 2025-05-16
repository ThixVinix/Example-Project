package com.example.exampleproject.configs.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Represents a response object for conveying details of multiple validation errors encountered during API processing.
 * This record provides a structured format for reporting multiple field-specific validation issues in an HTTP response.
 * <p>
 * <strong>The response includes:</strong>
 * <ul>
 *   <li><strong>Timestamp:</strong> The time when the errors occurred.</li>
 *   <li><strong>Path:</strong> The request path that triggered the errors.</li>
 *   <li><strong>HTTP Status Code:</strong> The status code representing the error category
 *   (e.g., 400 for "Bad Request").</li>
 *   <li><strong>Error Description:</strong> A short description of the error type.</li>
 *   <li><strong>Messages:</strong> A map containing detailed messages for each field with validation errors.</li>
 * </ul>
 * <p>
 * This record implements the {@link BaseError} interface to ensure consistent error handling
 * across different error response structures.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Response object containing details for multiple validation errors.")
public record ErrorMultipleResponse(LocalDateTime timestamp,

                                    String path,

                                    int status,

                                    String error,

                                    @Schema(description = "Detailed messages for each field with validation errors.",
                                            example = "{\"field1\": \"must not be blank\", \"field2\": \"must be a " +
                                                    "valid email\"}")
                                    Map<String, String> messages
) implements BaseError {
}
