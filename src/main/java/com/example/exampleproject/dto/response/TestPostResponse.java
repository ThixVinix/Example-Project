package com.example.exampleproject.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Builder
public record TestPostResponse(

        @JsonProperty("data")
        Date date,

        @JsonProperty("dataLocalHora")
        LocalDateTime localDateTime,

        @JsonProperty("dataLocal")
        LocalDate localDate,

        @JsonProperty("ZonaDataHora")
        ZonedDateTime zonedDateTime,

        @JsonProperty("horaLocal")
        LocalTime localTime
) {
}
