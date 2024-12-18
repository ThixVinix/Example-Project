package com.example.exampleproject.dto.request;

import com.example.exampleproject.configs.annotations.ValidDateRange;

import com.example.exampleproject.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ValidDateRange(dateAField = "dataInicial", dateBField = "dataFinal")
@Schema(description = "Representation of a request to create a new resource with validations and mandatory data.")
public record TestPostRequest(

        @JsonProperty("dataInicial")
        @Schema(description = "Start date of the period.", example = "2023-12-01 10:15:30",
                pattern = DateUtils.LOCAL_DATE_TIME_DESERIALIZER_FORMAT)
        LocalDateTime initialDate,

        @JsonProperty("dataFinal")
        @Schema(description = "End date of the period.", example = "2023-12-31 18:00:00",
                pattern = DateUtils.LOCAL_DATE_TIME_DESERIALIZER_FORMAT)
        LocalDateTime finalDate,

        @NotBlank
        @JsonProperty(value = "nome", required = true)
        @Schema(description = "Full name of the applicant.", example = "João Silva")
        String name,

        @NotNull
        @Past
        @JsonProperty(value = "dataNascimento", required = true)
        @Schema(description = "Applicant's date of birth (must be in the past).", example = "1990-05-15",
                pattern = DateUtils.LOCAL_DATE_DESERIALIZER_FORMAT)
        LocalDate dateOfBirth,

        @NotBlank
        @Email
        @JsonProperty(value = "email", required = true)
        @Schema(description = "Valid email of the applicant.", example = "joao.silva@email.com")
        String email,

        @NotBlank
        @CPF
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "{msg.validation.request.field.cpf.invalidFormat}")
        @JsonProperty(value = "cpf", required = true)
        @Schema(description = "CPF of the applicant in a valid format.", example = "123.456.789-00")
        String cpf,

        @NotNull
        @Min(value = 0)
        @Max(value = Byte.MAX_VALUE)
        @JsonProperty(value = "idade", required = true)
        @Schema(description = "Age of the applicant.", example = "34")
        Byte age,

        @NotBlank
        @JsonProperty(value = "imagemBase64", required = true)
        @Schema(description = "Base64 encoded image.", example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABA...")
        String base64Image,

        @NotBlank
        @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
                message = "{msg.validation.request.field.phoneNumber.invalidFormat}")
        @JsonProperty(value = "telefone", required = true)
        @Schema(description = "Phone number with valid format.", example = "11987654321")
        String telephone,

        @Size(min = 3, max = 30)
        @JsonProperty("username")
        @Schema(description = "Username for login.", example = "joaosilva")
        String username,

        @Future
        @NotNull
        @JsonProperty(value = "dataDeValidade", required = true)
        @Schema(description = "Future date indicating validity.", example = "2024-12-01",
                pattern = DateUtils.LOCAL_DATE_DESERIALIZER_FORMAT)
        LocalDate expirationDate,

        @DecimalMin(value = "0.0", inclusive = false)
        @Digits(integer = 5, fraction = 2)
        @JsonProperty("preco")
        @Schema(description = "Item price. It must be greater than zero.", example = "12345.67")
        BigDecimal price,

        @NotEmpty
        @JsonProperty("itens")
        @Schema(description = "List of items included in the requisition.", example = "[\"item1\", \"item2\"]")
        List<String> items,

        @AssertTrue
        @JsonProperty("termosConcordados")
        @Schema(description = "Confirmation that the terms have been accepted.", example = "true")
        Boolean isTermsAgreed
) {
}