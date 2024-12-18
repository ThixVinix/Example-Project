package com.example.exampleproject.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Builder
@Schema(description = "Represents the response from a test post endpoint, containing various date and time formats.")
public record TestPostResponse(

        @JsonProperty("data")
        @Schema(description = "The current system date and time.", example = "2023-11-04T12:45:30.000+00:00")
        Date date,

        @JsonProperty("dataLocalHora")
        @Schema(description = "The current local date and time in 'yyyy-MM-dd'T'HH:mm:ss' format.",
                example = "2023-11-04T12:45:30")
        LocalDateTime localDateTime,

        @JsonProperty("dataLocal")
        @Schema(description = "The current local date in 'yyyy-MM-dd' format.", example = "2023-11-04")
        LocalDate localDate,

        @JsonProperty("ZonaDataHora")
        @Schema(description = "The current date and time with timezone in ISO-8601 format.",
                example = "2023-11-04T12:45:30.000Z")
        ZonedDateTime zonedDateTime,

        @JsonProperty("horaLocal")
        @Schema(description = "The current local time in 'HH:mm:ss' format.", example = "12:45:30")
        LocalTime localTime
) {
}