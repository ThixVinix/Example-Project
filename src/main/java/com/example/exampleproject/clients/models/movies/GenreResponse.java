package com.example.exampleproject.clients.models.movies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreResponse {
    private Integer id;
    private String name;
}