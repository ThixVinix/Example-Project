package com.example.exampleproject.controllers;

import com.example.exampleproject.clients.models.JsonPlaceholderPost;
import com.example.exampleproject.services.JsonPlaceholderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "JsonPlaceholder", description = "Endpoints to manage JsonPlaceholder posts.")
@Validated
@RestController(value = "jsonplaceholder")
public class JsonPlaceholderController {

    private final JsonPlaceholderService jsonPlaceholderService;

    @Autowired
    public JsonPlaceholderController(JsonPlaceholderService jsonPlaceholderService) {
        this.jsonPlaceholderService = jsonPlaceholderService;
    }

    @Operation(
            operationId = "getPostById",
            summary = "Fetch a post by its ID",
            description = "Retrieves a specific post using its unique identifier from the JsonPlaceholder service."
    )
    @ApiResponse(
            responseCode = "200", description = "Post successfully retrieved",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = JsonPlaceholderPost.class))
    )
    @GetMapping("/posts/{id}")
    public JsonPlaceholderPost getPostById(
            @Parameter(description = "Unique identifier of the post to be retrieved", example = "1", required = true)
            @PathVariable("id")
            Long id) {
        return jsonPlaceholderService.getPostById(id);
    }

    @Operation(
            operationId = "createPost",
            summary = "Create a new post",
            description = "Creates a new post in the JsonPlaceholder service with the provided data."
    )
    @ApiResponse(
            responseCode = "200", description = "Post successfully created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = JsonPlaceholderPost.class))
    )
    @PostMapping("/posts")
    public JsonPlaceholderPost createPost(
            @Parameter(description = "Details of the post to be created", required = true)
            @Valid
            @RequestBody
            JsonPlaceholderPost post) {
        return jsonPlaceholderService.createPost(post);
    }

    @Operation(
            operationId = "deletePost",
            summary = "Delete a post",
            description = "Deletes an existing post in the JsonPlaceholder service using its unique identifier."
    )
    @ApiResponse(
            responseCode = "200", description = "Post successfully deleted"
    )
    @DeleteMapping("/posts/{id}")
    public void deletePost(
            @Parameter(description = "Unique identifier of the post to be deleted", example = "1", required = true)
            @PathVariable("id")
            Long id) {
        jsonPlaceholderService.deletePost(id);
    }

    @Operation(
            operationId = "updatePost",
            summary = "Update a post",
            description = "Updates an existing post with new details in the JsonPlaceholder service."
    )
    @ApiResponse(
            responseCode = "200", description = "Post successfully updated",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = JsonPlaceholderPost.class))
    )
    @PatchMapping("/posts/{id}")
    public JsonPlaceholderPost updatePost(
            @Parameter(description = "Unique identifier of the post to be updated", example = "1", required = true)
            @PathVariable("id")
            Long id,

            @Parameter(description = "Updated details of the post", required = true)
            @Valid
            @RequestBody
            JsonPlaceholderPost post) {
        return jsonPlaceholderService.updatePost(id, post);
    }

}
