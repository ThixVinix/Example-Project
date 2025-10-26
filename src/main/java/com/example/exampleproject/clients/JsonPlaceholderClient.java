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

    public static final MediaType APPLICATION_JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON;

    private final WebClient jsonPlaceholderWebClient;

    public JsonPlaceholderPost getPostById(Long id) {
        return jsonPlaceholderWebClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(POST_ID_URI).build(id))
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .retrieve()
                .bodyToMono(JsonPlaceholderPost.class)
                .block();
    }

    public JsonPlaceholderPost createPost(JsonPlaceholderPost post) {
        return jsonPlaceholderWebClient
                .post()
                .uri(POSTS_URI)
                .contentType(APPLICATION_JSON_MEDIA_TYPE)
                .accept(APPLICATION_JSON_MEDIA_TYPE)
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
                .contentType(APPLICATION_JSON_MEDIA_TYPE)
                .accept(APPLICATION_JSON_MEDIA_TYPE)
                .bodyValue(post)
                .retrieve()
                .bodyToMono(JsonPlaceholderPost.class)
                .block();
    }
}
