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
@Schema(description = "Detalhes completos de um filme")
public class MovieDetailsDto {

    @JsonProperty("adulto")
    @Schema(description = "Indica se o conteúdo é para adultos")
    private Boolean adult;

    @JsonProperty("caminhoFundo")
    @Schema(description = "Caminho da imagem de fundo")
    private String backdropPath;

    @JsonProperty("colecaoPertencente")
    @Schema(description = "Coleção à qual o filme pertence")
    private BelongsToCollectionDto belongsToCollection;

    @JsonProperty("orcamento")
    @Schema(description = "Orçamento do filme")
    private Integer budget;

    @JsonProperty("generos")
    @Schema(description = "Lista de gêneros do filme")
    private List<GenreDto> genres;

    @JsonProperty("paginaInicial")
    @Schema(description = "Página inicial do filme")
    private String homepage;

    @JsonProperty("id")
    @Schema(description = "Identificador do filme")
    private Integer id;

    @JsonProperty("imdbId")
    @Schema(description = "Identificador no IMDB")
    private String imdbId;

    @JsonProperty("paisesOrigem")
    @Schema(description = "Países de origem")
    private List<String> originCountry;

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

    @JsonProperty("empresasProducao")
    @Schema(description = "Empresas de produção")
    private List<ProductionCompanyDto> productionCompanies;

    @JsonProperty("paisesProducao")
    @Schema(description = "Países de produção")
    private List<ProductionCountryDto> productionCountries;

    @JsonProperty("dataLancamento")
    @Schema(description = "Data de lançamento")
    private String releaseDate;

    @JsonProperty("receita")
    @Schema(description = "Receita do filme")
    private Long revenue;

    @JsonProperty("duracao")
    @Schema(description = "Duração (minutos)")
    private Integer runtime;

    @JsonProperty("idiomasFalados")
    @Schema(description = "Idiomas falados no filme")
    private List<SpokenLanguageDto> spokenLanguages;

    @JsonProperty("status")
    @Schema(description = "Status do filme")
    private String status;

    @JsonProperty("fraseEfeito")
    @Schema(description = "Tagline/Frase de efeito")
    private String tagline;

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
