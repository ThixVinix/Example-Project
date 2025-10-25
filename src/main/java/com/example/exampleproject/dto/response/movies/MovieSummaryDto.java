package com.example.exampleproject.dto.response.movies;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumo das informações de um filme")
public class MovieSummaryDto {

    @JsonProperty("adulto")
    @Schema(description = "Indica se o conteúdo é para adultos")
    private Boolean adult;

    @JsonProperty("caminhoFundo")
    @Schema(description = "Caminho da imagem de fundo")
    private String backdropPath;

    @JsonProperty("idsGeneros")
    @Schema(description = "Lista de IDs dos gêneros")
    private List<Integer> genreIds;

    @JsonProperty("id")
    @Schema(description = "Identificador do filme")
    private Integer id;

    @JsonProperty("idiomaOriginal")
    @Schema(description = "Idioma original do filme")
    private String originalLanguage;

    @JsonProperty("tituloOriginal")
    @Schema(description = "Título original do filme")
    private String originalTitle;

    @JsonProperty("resumo")
    @Schema(description = "Resumo/sinopse do filme")
    private String overview;

    @JsonProperty("popularidade")
    @Schema(description = "Índice de popularidade")
    private Double popularity;

    @JsonProperty("caminhoPoster")
    @Schema(description = "Caminho do poster")
    private String posterPath;

    @JsonProperty("dataLancamento")
    @Schema(description = "Data de lançamento (AAAA-MM-DD)")
    private String releaseDate;

    @JsonProperty("titulo")
    @Schema(description = "Título do filme")
    private String title;

    @JsonProperty("video")
    @Schema(description = "Indica se há vídeo")
    private Boolean video;

    @JsonProperty("mediaVotos")
    @Schema(description = "Média de votos")
    private Double voteAverage;

    @JsonProperty("quantidadeVotos")
    @Schema(description = "Quantidade de votos")
    private Integer voteCount;
}
