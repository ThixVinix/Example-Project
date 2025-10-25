package com.example.exampleproject.clients.models.movies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreListResponse {
    private List<GenreResponse> genres;
}