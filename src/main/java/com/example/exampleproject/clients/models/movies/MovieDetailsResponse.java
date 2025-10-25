package com.example.exampleproject.clients.models.movies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDetailsResponse {
    private Boolean adult;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("belongs_to_collection")
    private BelongsToCollectionResponse belongsToCollection;

    private Integer budget;

    private List<GenreResponse> genres;

    private String homepage;

    private Integer id;

    @JsonProperty("imdb_id")
    private String imdbId;

    @JsonProperty("origin_country")
    private List<String> originCountry;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_title")
    private String originalTitle;

    private String overview;

    private Double popularity;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("production_companies")
    private List<ProductionCompanyResponse> productionCompanies;

    @JsonProperty("production_countries")
    private List<ProductionCountryResponse> productionCountries;

    @JsonProperty("release_date")
    private String releaseDate;

    private Long revenue;

    private Integer runtime;

    @JsonProperty("spoken_languages")
    private List<SpokenLanguageResponse> spokenLanguages;

    private String status;

    private String tagline;

    private String title;

    private Boolean video;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;
}
