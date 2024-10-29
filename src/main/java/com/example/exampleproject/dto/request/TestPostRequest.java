package com.example.exampleproject.dto.request;

import com.example.exampleproject.configs.annotations.ValidDateRange;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ValidDateRange(dateAField = "dataInicial", dateBField = "dataFinal")
public record TestPostRequest(

        @JsonProperty("dataInicial")
        LocalDateTime initialDate,

        @JsonProperty("dataFinal")
        LocalDateTime finalDate,

        @NotBlank
        @JsonProperty("nome")
        String name,

        @NotNull
        @Past
        @JsonProperty("dataNascimento")
        LocalDate dateOfBirth,

        @NotBlank
        @Email
        @JsonProperty("email")
        String email,

        @NotBlank
        @CPF
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "{msg.validation.request.field.cpf.invalidFormat}")
        @JsonProperty("cpf")
        String cpf,

        @NotNull
        @Min(value = 0)
        @Max(value = Byte.MAX_VALUE)
        @JsonProperty("idade")
        Byte age,

        @NotBlank
        @JsonProperty("imagemBase64")
        String base64Image,

        @NotBlank
        @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
                message = "{msg.validation.request.field.phoneNumber.invalidFormat}")
        @JsonProperty("telefone")
        String telephone,

        @Size(min = 3, max = 30)
        @JsonProperty("username")
        String username,

        @Future
        @NotNull
        @JsonProperty("dataDeValidade")
        LocalDate expirationDate,

        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 5, fraction = 2)
        @JsonProperty("preco")
        BigDecimal price,

        @NotEmpty
        @JsonProperty("itens")
        List<String> items,

        @AssertTrue
        @JsonProperty("termosConcordados")
        Boolean isTermsAgreed
) {
}