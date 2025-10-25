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
@Schema(description = "Idioma falado no filme")
public class SpokenLanguageDto {

    @JsonProperty("nomeIngles")
    @Schema(description = "Nome do idioma em inglês")
    private String englishName;

    @JsonProperty("iso6391")
    @Schema(description = "Código ISO 639-1 do idioma")
    private String iso6391;

    @JsonProperty("nome")
    @Schema(description = "Nome do idioma")
    private String name;
}
