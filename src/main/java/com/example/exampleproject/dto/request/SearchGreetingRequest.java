package com.example.exampleproject.dto.request;

import com.example.exampleproject.configs.annotations.DateRangeValidation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.BindParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@DateRangeValidation(dateAField = "initialDate", dateBField = "finalDate")
public record SearchGreetingRequest(

        @BindParam("dataInicial")
        @DateTimeFormat(pattern = "dd/MM/yyyy")
        @Parameter(name = "dataInicial", description = "Initial date. Format: dd/MM/yyyy", example = "01/01/2022",
                required = true)
        LocalDate initialDate,

        @BindParam("dataFinal")
        @DateTimeFormat(pattern = "dd/MM/yyyy")
        @Parameter(name = "dataFinal", description = "Final date. Format: dd/MM/yyyy", example = "31/12/2022",
                required = true)
        LocalDate finalDate,

        @BindParam("dataLocalDataTempo")
        @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        @Parameter(name = "dataLocalDataTempo", description = "Local date and time. Format: dd/MM/yyyy HH:mm:ss",
                example = "01/01/2022 00:00:00", required = true)
        LocalDateTime localDateTime,

        @BindParam("zonaDataTempo")
        @Parameter(name = "zonaDataTempo",
                description = "Date and time with timezone. Format: yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                example = "2023-11-04T15:20:30.123555Z")
        ZonedDateTime zonedDateTime,

        @NotNull
        @BindParam("tempoLocal")
        @DateTimeFormat(pattern = "HH:mm:ss")
        @Parameter(name = "tempoLocal", description = "Local time. Format: HH:mm:ss", example = "00:00:00",
                schema = @Schema(type = "string"))
        LocalTime localTime,

        @BindParam("idade")
        @Min(value = 0)
        @Max(value = Byte.MAX_VALUE)
        @Parameter(name = "idade", description = "Age, minimum of 0 to maximum of 127", example = "65", required = true)
        Long age,

        @NotNull
        @BindParam("preco")
        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 1, fraction = 2)
        @Parameter(name = "preco",
                description = "Price, must be greater than 0.0 with up to 1 integer digit and 2 fractional " +
                "digits. For example, 1.42 is valid.", example = "6.23", required = true)
        BigDecimal price
) {
}
