package com.example.exampleproject.controllers;

import com.example.exampleproject.clients.models.JsonPlaceholderPost;
import com.example.exampleproject.services.JsonPlaceholderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController(value = "jsonplaceholder")
public class JsonPlaceholderController {

    private final JsonPlaceholderService jsonPlaceholderService;

    @Autowired
    public JsonPlaceholderController(JsonPlaceholderService jsonPlaceholderService) {
        this.jsonPlaceholderService = jsonPlaceholderService;
    }

    @GetMapping("/posts/{id}")
    public JsonPlaceholderPost getPostById(@PathVariable("id") Long id) {
        return jsonPlaceholderService.getPostById(id);
    }

    @PostMapping("/posts")
    public JsonPlaceholderPost createPost(@Valid @RequestBody JsonPlaceholderPost post) {
        return jsonPlaceholderService.createPost(post);
    }

    @DeleteMapping("/posts/{id}")
    public void deletePost(@PathVariable("id") Long id) {
        jsonPlaceholderService.deletePost(id);
    }

    @PatchMapping("/posts/{id}")
    public JsonPlaceholderPost updatePost(@PathVariable("id") Long id, @Valid @RequestBody JsonPlaceholderPost post) {
        return jsonPlaceholderService.updatePost(id, post);
    }

}
