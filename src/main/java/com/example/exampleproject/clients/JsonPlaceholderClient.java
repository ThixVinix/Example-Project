package com.example.exampleproject.clients;

import com.example.exampleproject.clients.models.JsonPlaceholderPost;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class JsonPlaceholderClient {

    public static final String POST_ID_URI = "/posts/{id}";
    public static final String POSTS_URI = "/posts";

    private final WebClient jsonPlaceholderWebClient;

    public JsonPlaceholderPost getPostById(Long id) {
        return jsonPlaceholderWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(POST_ID_URI).build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonPlaceholderPost.class)
                .block();
    }

    public JsonPlaceholderPost createPost(JsonPlaceholderPost post) {
        return jsonPlaceholderWebClient
                .post()
                .uri(POSTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(post)
                .retrieve()
                .bodyToMono(JsonPlaceholderPost.class)
                .block();
    }

    public void deletePost(Long id) {
        jsonPlaceholderWebClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(POST_ID_URI).build(id))
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public JsonPlaceholderPost updatePost(Long id, JsonPlaceholderPost post) {
        return jsonPlaceholderWebClient
                .patch()
                .uri(uriBuilder -> uriBuilder.path(POST_ID_URI).build(id))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(post)
                .retrieve()
                .bodyToMono(JsonPlaceholderPost.class)
                .block();
    }
}
