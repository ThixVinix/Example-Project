package com.example.exampleproject.dto.response.movies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "País de produção do filme")
public class ProductionCountryDto {

    @JsonProperty("iso31661")
    @Schema(description = "Código ISO 3166-1 do país")
    private String iso31661;

    @JsonProperty("nome")
    @Schema(description = "Nome do país")
    private String name;
}
