package com.example.exampleproject.configs.exceptions;

import com.example.exampleproject.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

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
