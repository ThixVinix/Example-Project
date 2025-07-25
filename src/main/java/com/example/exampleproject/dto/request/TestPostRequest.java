package com.example.exampleproject.dto.request;

import com.example.exampleproject.configs.annotations.*;

import com.example.exampleproject.enums.StatusEnum;
import com.example.exampleproject.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@DateRangeValidation(dateAField = "initialDate", dateBField = "finalDate")
@Schema(description = "Representation of a request to create a new resource with validations and mandatory data.")
public record TestPostRequest(

        @JsonProperty("dataInicial")
        @Schema(description = "Start date of the period.", example = "2023-12-01 10:15:30",
                pattern = DateUtils.LOCAL_DATE_TIME_DESERIALIZER_FORMAT, implementation = String.class)
        LocalDateTime initialDate,

        @JsonProperty("dataFinal")
        @Schema(description = "End date of the period.", example = "2023-12-31 18:00:00",
                pattern = DateUtils.LOCAL_DATE_TIME_DESERIALIZER_FORMAT, implementation = String.class)
        LocalDateTime finalDate,

        @NotBlank
        @JsonProperty(value = "nome", required = true)
        @Schema(description = "Full name of the applicant.", example = "João Silva")
        String name,

        @NotNull
        @Past
        @JsonProperty(value = "dataNascimento", required = true)
        @Schema(description = "Applicant's date of birth (must be in the past).", example = "1991-09-05",
                pattern = DateUtils.LOCAL_DATE_DESERIALIZER_FORMAT)
        LocalDate dateOfBirth,

        @NotBlank
        @Email
        @JsonProperty(value = "email", required = true)
        @Schema(description = "Valid email of the applicant.", example = "joao.silva@email.com")
        String email,

        @NotBlank
        @CpfCnpjValidation
        @JsonProperty("cpfCnpj")
        @Schema(description = "CPF or CNPJ of the applicant. Examples: CPF '81865839043' or CNPJ '09795208000119'.",
                example = "09795208000119")
        String cpfCnpj,

        @NotNull
        @Min(value = 0)
        @Max(value = Byte.MAX_VALUE)
        @JsonProperty(value = "idade", required = true)
        @Schema(description = "Age of the applicant.", example = "34", implementation = String.class)
        Byte age,

        @NotBlank
        @Pattern(regexp = "\\(\\d{2}\\) \\d{4,5}-\\d{4}",
                message = "{msg.validation.request.field.phoneNumber.invalidFormat}")
        @JsonProperty(value = "telefone", required = true)
        @Schema(description = "Phone number with valid format.", example = "(11) 98765-4321")
        String telephone,

        @Size(min = 3, max = 30)
        @JsonProperty("username")
        @Schema(description = "Username for login.", example = "joaosilva")
        String username,

        @Future
        @NotNull
        @JsonProperty(value = "dataDeValidade", required = true)
        @Schema(description = "Future date indicating validity.", example = "2999-12-01",
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
        Boolean isTermsAgreed,

        @NotNull
        @JsonProperty("status")
        @EnumValueValidation(enumClass = StatusEnum.class)
        @Schema(description = "Status description from the enumeration.", example = "ATIVO")
        String statusValueEnum,

        @NotNull
        @JsonProperty("codigo")
        @EnumCodeValidation(enumClass = StatusEnum.class)
        @Schema(description = "Status code from the enumeration.", example = "1")
        Integer statusCodeEnum,

        @NotBlank
        @Base64FileValidation(
                allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"},
                maxSizeInMB = 3)
        @JsonProperty(value = "imagemBase64", required = true)
        @Schema(description = "Base64 encoded image. Expected format: data:[type]/[subtype];base64,[content]",
                example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQF" +
                        "QYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC")
        String base64Image,

        @NotEmpty
        @Base64FileValidation(
                allowedTypes = {"application/pdf", "text/plain"},
                maxSizeInMB = 4,
                maxFileCount = 3
        )
        @JsonProperty("listaBase64")
        @Schema(description = "List of documents encoded in Base64. " +
                "Expected format: data:[type]/[subtype];base64,[content]",
                example = "[\"data:application/pdf;base64,JVBERi0xLjMKJ1RyYWlsZXInCiUlRU9GCg==\", " +
                        "\"data:text/plain;base64,VGVzdCBjb250ZW50\"]")
        List<String> base64List,

        @NotEmpty
        @Base64FileValidation(
                allowedTypes = {"application/pdf", "text/plain"},
                maxSizeInMB = 4,
                maxFileCount = 3
        )
        @JsonProperty("mapaBase64")
        @Schema(description = "File Map where the key is the file name (with extension) and the value is the " +
                "content coded in Base64. Expected format: data:[type]/[subtype];base64,[content]",
                example = "{\"document1.pdf\": \"data:application/pdf;base64,JVBERi0xLjMKJ1RyYWlsZXInCiUlRU9GCg==\", " +
                        "\"document2.txt\": \"data:text/plain;base64,VGVzdCBjb250ZW50\"}")
        Map<String, String> base64Map

) {
}
