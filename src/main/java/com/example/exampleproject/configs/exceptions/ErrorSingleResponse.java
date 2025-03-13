package com.example.exampleproject.configs.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

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
