package com.example.exampleproject.dto.request;

import com.example.exampleproject.configs.annotations.ValidDateRange;
import com.example.exampleproject.configs.deserializers.CustomLocalDateDeserializer;
import com.example.exampleproject.configs.deserializers.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ValidDateRange(dateAField = "dataInicial", dateBField = "dataFinal")
public record TestPostRequest(

        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        LocalDateTime dataInicial,

        @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
        LocalDateTime dataFinal,

        @NotBlank
        String nome,

        @NotNull
        @Past
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        LocalDate dataNascimento,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @CPF
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "{msg.validation.request.field.cpf.invalidFormat}")
        String cpf,

        @NotNull
        @Min(value = 0)
        @Max(value = Byte.MAX_VALUE)
        Byte idade,

        @NotBlank
        String imagemBase64,

        @NotBlank
        @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
                message = "{msg.validation.request.field.phoneNumber.invalidFormat}")
        String telefone,

        @Size(min = 3, max = 30)
        String username,

        @Future
        @NotNull
        @JsonDeserialize(using = CustomLocalDateDeserializer.class)
        LocalDate dataDeValidade,

        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 5, fraction = 2)
        BigDecimal preco
) {
}