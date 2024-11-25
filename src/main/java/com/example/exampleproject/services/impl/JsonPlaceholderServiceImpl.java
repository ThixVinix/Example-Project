package com.example.exampleproject.services.impl;

import com.example.exampleproject.clients.JsonPlaceholderClient;
import com.example.exampleproject.clients.models.JsonPlaceholderPost;
import com.example.exampleproject.services.JsonPlaceholderService;
import org.springframework.stereotype.Service;

@Service
public class JsonPlaceholderServiceImpl implements JsonPlaceholderService {


    private final JsonPlaceholderClient jsonPlaceholderClient;

    public JsonPlaceholderServiceImpl(JsonPlaceholderClient jsonPlaceholderClient) {
        this.jsonPlaceholderClient = jsonPlaceholderClient;
    }

    @Override
    public JsonPlaceholderPost getPostById(Long id) {
        return jsonPlaceholderClient.getPostById(id);
    }

    @Override
    public JsonPlaceholderPost createPost(JsonPlaceholderPost post) {
        return jsonPlaceholderClient.createPost(post);
    }

    @Override
    public void deletePost(Long id) {
        jsonPlaceholderClient.deletePost(id);
    }

    @Override
    public JsonPlaceholderPost updatePost(Long id, JsonPlaceholderPost post) {
        return jsonPlaceholderClient.updatePost(id, post, "PATCH");
    }
}
