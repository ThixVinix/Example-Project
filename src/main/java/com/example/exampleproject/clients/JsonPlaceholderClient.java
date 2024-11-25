package com.example.exampleproject.clients;

import com.example.exampleproject.clients.models.JsonPlaceholderPost;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "jsonplaceholder", url = "https://jsonplaceholder.typicode.com")
public interface JsonPlaceholderClient {

    @GetMapping("/posts/{id}")
    JsonPlaceholderPost getPostById(@PathVariable("id") Long id);

    @PostMapping("/posts")
    JsonPlaceholderPost createPost(@RequestBody JsonPlaceholderPost post);

    @DeleteMapping("/posts/{id}")
    void deletePost(@PathVariable("id") Long id);

    @PostMapping("/posts/{id}")
    JsonPlaceholderPost updatePost(@PathVariable("id") Long id,
                                   @RequestBody JsonPlaceholderPost post,
                                   @RequestHeader("X-HTTP-Method-Override") String methodOverride);

}
