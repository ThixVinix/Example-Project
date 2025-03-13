package com.example.exampleproject.configs.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

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
