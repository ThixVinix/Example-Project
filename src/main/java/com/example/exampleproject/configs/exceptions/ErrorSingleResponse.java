package com.example.exampleproject.configs.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Represents a response object for conveying details of a single error encountered during API processing.
 * This record provides a structured format for reporting single error scenarios in an HTTP response.
 * <p>
 * <strong>The response includes:</strong>
 * <ul>
 *   <li><strong>Timestamp:</strong> The time when the error occurred.</li>
 *   <li><strong>Path:</strong> The request path that triggered the error.</li>
 *   <li><strong>HTTP Status Code:</strong> The status code representing the error category
 *   (e.g., 404 for "Not Found").</li>
 *   <li><strong>Error Description:</strong> A short description of the error type.</li>
 *   <li><strong>Message:</strong> A detailed message providing additional context about the specific error.</li>
 * </ul>
 * <p>
 * This record implements the {@link BaseError} interface to ensure consistent error handling
 * across different error response structures.
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Response object containing details for a single error.")
public record ErrorSingleResponse(LocalDateTime timestamp,

                                  String path,

                                  int status,

                                  String error,

                                  @Schema(description = "Detailed message providing additional information about " +
                                          "the error.",
                                          example = "The requested resource was not found.")
                                  String message
) implements BaseError {
}
