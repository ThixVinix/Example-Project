package com.example.exampleproject.configs.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(LocalDateTime timestamp,
                            String path,
                            int status,
                            String error,
                            Map<String, String> messages
) {
}
