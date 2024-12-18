package com.example.exampleproject.clients.models;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a JsonPlaceholder Post with details like title and body.")
public record JsonPlaceholderPost(
        @Schema(description = "Unique identifier of the post.", example = "1")
        Long id,

        @Schema(description = "ID of the user who created the post.", example = "10")
        Long userId,

        @Schema(description = "Title of the post.", example = "Introduction to Swagger")
        String title,

        @Schema(description = "The body/content of the post.",
                example = "This is an example post about Swagger annotations.")
        String body
) {
}