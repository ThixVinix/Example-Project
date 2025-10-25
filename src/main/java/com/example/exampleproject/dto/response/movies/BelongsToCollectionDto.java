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
@Schema(description = "Coleção à qual o filme pertence")
public class BelongsToCollectionDto {

    @JsonProperty("id")
    @Schema(description = "Identificador da coleção")
    private Integer id;

    @JsonProperty("nome")
    @Schema(description = "Nome da coleção")
    private String name;

    @JsonProperty("caminhoPoster")
    @Schema(description = "Caminho do poster da coleção")
    private String posterPath;

    @JsonProperty("caminhoFundo")
    @Schema(description = "Caminho da imagem de fundo da coleção")
    private String backdropPath;
}
