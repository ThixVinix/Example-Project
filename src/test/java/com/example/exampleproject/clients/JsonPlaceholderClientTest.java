package com.example.exampleproject.clients;

import com.example.exampleproject.clients.models.JsonPlaceholderPost;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for class {@link JsonPlaceholderClient}
 */
@Tag(value = "JsonPlaceholderClient_Tests")
@DisplayName("JsonPlaceholderClient Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JsonPlaceholderClientTest {

    private MockWebServer mockWebServer;

    private static final String GET_POST_BY_ID = "getPostById";
    private static final String CREATE_POST = "createPost";
    private static final String DELETE_POST = "deletePost";
    private static final String UPDATE_POST = "updatePost";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private JsonPlaceholderClient buildClientAgainstMockServer() {
        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
        return new JsonPlaceholderClient(webClient);
    }

    @Order(1)
    @Test
    @Tag(value = GET_POST_BY_ID)
    @DisplayName(GET_POST_BY_ID + " - Should GET /posts/{id} and map response")
    void testGetPostById_ShouldReturnPostAndCorrectRequest() throws InterruptedException {
        // Arrange
        String body = """
{
  "id": 1,
  "userId": 10,
  "title": "Hello",
  "body": "World"
}
""";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(body));

        JsonPlaceholderClient client = buildClientAgainstMockServer();

        // Act
        JsonPlaceholderPost response = client.getPostById(1L);

        // Assert mapping
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(10L, response.userId());
        assertEquals("Hello", response.title());
        assertEquals("World", response.body());

        // Assert request
        RecordedRequest recorded = mockWebServer.takeRequest();
        assertEquals("GET", recorded.getMethod());
        assertEquals("/posts/1", recorded.getPath());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recorded.getHeader("Accept"));
    }

    @Order(2)
    @Test
    @Tag(value = CREATE_POST)
    @DisplayName(CREATE_POST + " - Should POST /posts with JSON body and map response")
    void testCreatePost_ShouldSendBodyAndMapResponse() throws InterruptedException {
        // Arrange
        String responseBody = """
{
  "id": 101,
  "userId": 55,
  "title": "Created",
  "body": "New post"
}
""";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        JsonPlaceholderClient client = buildClientAgainstMockServer();
        JsonPlaceholderPost requestPost = new JsonPlaceholderPost(null, 55L, "Created", "New post");

        // Act
        JsonPlaceholderPost response = client.createPost(requestPost);

        // Assert mapping
        assertNotNull(response);
        assertEquals(101L, response.id());
        assertEquals(55L, response.userId());
        assertEquals("Created", response.title());
        assertEquals("New post", response.body());

        // Assert request
        RecordedRequest recorded = mockWebServer.takeRequest();
        assertEquals("POST", recorded.getMethod());
        assertEquals("/posts", recorded.getPath());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recorded.getHeader("Content-Type"));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recorded.getHeader("Accept"));
        String sentBody = recorded.getBody().readUtf8();
        assertTrue(sentBody.contains("\"userId\":55"));
        assertTrue(sentBody.contains("\"title\":\"Created\""));
        assertTrue(sentBody.contains("\"body\":\"New post\""));
    }

    @Order(3)
    @Test
    @Tag(value = UPDATE_POST)
    @DisplayName(UPDATE_POST + " - Should PATCH /posts/{id} with JSON body and map response")
    void testUpdatePost_ShouldSendBodyAndMapResponse() throws InterruptedException {
        // Arrange
        String responseBody = """
{
  "id": 5,
  "userId": 70,
  "title": "Updated",
  "body": "Patched"
}
""";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(responseBody));

        JsonPlaceholderClient client = buildClientAgainstMockServer();
        JsonPlaceholderPost patchPost = new JsonPlaceholderPost(5L, 70L, "Updated", "Patched");

        // Act
        JsonPlaceholderPost response = client.updatePost(5L, patchPost);

        // Assert mapping
        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals(70L, response.userId());
        assertEquals("Updated", response.title());
        assertEquals("Patched", response.body());

        // Assert request
        RecordedRequest recorded = mockWebServer.takeRequest();
        assertEquals("PATCH", recorded.getMethod());
        assertEquals("/posts/5", recorded.getPath());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recorded.getHeader("Content-Type"));
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recorded.getHeader("Accept"));
        String sentBody = recorded.getBody().readUtf8();
        assertTrue(sentBody.contains("\"id\":5"));
        assertTrue(sentBody.contains("\"userId\":70"));
        assertTrue(sentBody.contains("\"title\":\"Updated\""));
        assertTrue(sentBody.contains("\"body\":\"Patched\""));
    }

    @Order(4)
    @Test
    @Tag(value = DELETE_POST)
    @DisplayName(DELETE_POST + " - Should DELETE /posts/{id} with no body and handle 200/204")
    void testDeletePost_ShouldSendDeleteAndSucceed() throws InterruptedException {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        JsonPlaceholderClient client = buildClientAgainstMockServer();

        // Act & Assert no exception
        assertDoesNotThrow(() -> client.deletePost(77L));

        // Assert request
        RecordedRequest recorded = mockWebServer.takeRequest();
        assertEquals("DELETE", recorded.getMethod());
        assertEquals("/posts/77", recorded.getPath());
    }

    @Order(5)
    @Test
    @Tag(value = GET_POST_BY_ID)
    @DisplayName(GET_POST_BY_ID + " - When server returns 500 then WebClientResponseException is thrown")
    void testGetPostById_ServerError_ShouldThrowWebClientResponseException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                .setBody("Server error"));

        JsonPlaceholderClient client = buildClientAgainstMockServer();

        // Act & Assert
        var ex = assertThrows(WebClientResponseException.class, () -> client.getPostById(1L));
        assertEquals(500, ex.getStatusCode().value());
        String responseBody = new String(ex.getResponseBodyAsByteArray(), StandardCharsets.UTF_8);
        assertTrue(responseBody.contains("Server error"));
    }
}
