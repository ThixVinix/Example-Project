package com.example.exampleproject.mappers;

import com.example.exampleproject.clients.models.movies.*;
import com.example.exampleproject.dto.response.movies.*;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class MovieMapper {

    public GenreListDto toGenreListDto(GenreListResponse source) {
        if (source == null) {
            return null;
        }
        List<GenreDto> genres = nullSafeList(source.getGenres()).stream()
                .filter(Objects::nonNull)
                .map(this::toGenreDto)
                .toList();
        return GenreListDto.builder()
                .genres(genres)
                .build();
    }

    public MoviePagedDto toMoviePagedDto(MoviePagedResponse source) {
        if (source == null) {
            return null;
        }
        List<MovieSummaryDto> results = nullSafeList(source.getResults()).stream()
                .filter(Objects::nonNull)
                .map(this::toMovieSummaryDto)
                .toList();
        return MoviePagedDto.builder()
                .page(source.getPage())
                .results(results)
                .totalPages(source.getTotalPages())
                .totalResults(source.getTotalResults())
                .build();
    }

    public MovieDetailsDto toMovieDetailsDto(MovieDetailsResponse source) {
        if (source == null) {
            return null;
        }
        return MovieDetailsDto.builder()
                .adult(source.getAdult())
                .backdropPath(source.getBackdropPath())
                .belongsToCollection(toBelongsToCollectionDto(source.getBelongsToCollection()))
                .budget(source.getBudget())
                .genres(nullSafeList(source.getGenres()).stream().map(this::toGenreDto).toList())
                .homepage(source.getHomepage())
                .id(source.getId())
                .imdbId(source.getImdbId())
                .originCountry(source.getOriginCountry())
                .originalLanguage(source.getOriginalLanguage())
                .originalTitle(source.getOriginalTitle())
                .overview(source.getOverview())
                .popularity(source.getPopularity())
                .posterPath(source.getPosterPath())
                .productionCompanies(nullSafeList(source.getProductionCompanies())
                        .stream()
                        .map(this::toProductionCompanyDto)
                        .toList())
                .productionCountries(nullSafeList(source.getProductionCountries())
                        .stream()
                        .map(this::toProductionCountryDto)
                        .toList())
                .releaseDate(source.getReleaseDate())
                .revenue(source.getRevenue())
                .runtime(source.getRuntime())
                .spokenLanguages(nullSafeList(source.getSpokenLanguages())
                        .stream()
                        .map(this::toSpokenLanguageDto)
                        .toList())
                .status(source.getStatus())
                .tagline(source.getTagline())
                .title(source.getTitle())
                .video(source.getVideo())
                .voteAverage(source.getVoteAverage())
                .voteCount(source.getVoteCount())
                .build();
    }

    public MovieSummaryDto toMovieSummaryDto(MovieSummaryResponse source) {
        if (source == null) {
            return null;
        }
        return MovieSummaryDto.builder()
                .adult(source.getAdult())
                .backdropPath(source.getBackdropPath())
                .genreIds(source.getGenreIds())
                .id(source.getId())
                .originalLanguage(source.getOriginalLanguage())
                .originalTitle(source.getOriginalTitle())
                .overview(source.getOverview())
                .popularity(source.getPopularity())
                .posterPath(source.getPosterPath())
                .releaseDate(source.getReleaseDate())
                .title(source.getTitle())
                .video(source.getVideo())
                .voteAverage(source.getVoteAverage())
                .voteCount(source.getVoteCount())
                .build();
    }

    public GenreDto toGenreDto(GenreResponse source) {
        if (source == null) {
            return null;
        }
        return GenreDto.builder()
                .id(source.getId())
                .name(source.getName())
                .build();
    }

    public BelongsToCollectionDto toBelongsToCollectionDto(BelongsToCollectionResponse source) {
        if (source == null) {
            return null;
        }
        return BelongsToCollectionDto.builder()
                .id(source.getId())
                .name(source.getName())
                .posterPath(source.getPosterPath())
                .backdropPath(source.getBackdropPath())
                .build();
    }

    public ProductionCompanyDto toProductionCompanyDto(ProductionCompanyResponse source) {
        if (source == null) {
            return null;
        }
        return ProductionCompanyDto.builder()
                .id(source.getId())
                .logoPath(source.getLogoPath())
                .name(source.getName())
                .originCountry(source.getOriginCountry())
                .build();
    }

    public ProductionCountryDto toProductionCountryDto(ProductionCountryResponse source) {
        if (source == null) {
            return null;
        }
        return ProductionCountryDto.builder()
                .iso31661(source.getIso31661())
                .name(source.getName())
                .build();
    }

    public SpokenLanguageDto toSpokenLanguageDto(SpokenLanguageResponse source) {
        if (source == null) {
            return null;
        }
        return SpokenLanguageDto.builder()
                .englishName(source.getEnglishName())
                .iso6391(source.getIso6391())
                .name(source.getName())
                .build();
    }

    private <T> List<T> nullSafeList(List<T> list) {
        return Optional.ofNullable(list).orElse(Collections.emptyList());
    }
}
