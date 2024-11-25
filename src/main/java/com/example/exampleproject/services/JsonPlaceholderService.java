package com.example.exampleproject.services;

import com.example.exampleproject.clients.models.JsonPlaceholderPost;

public interface JsonPlaceholderService {

    JsonPlaceholderPost getPostById(Long id);

    JsonPlaceholderPost createPost(JsonPlaceholderPost post);

    void deletePost(Long id);

    JsonPlaceholderPost updatePost(Long id, JsonPlaceholderPost post);

}
