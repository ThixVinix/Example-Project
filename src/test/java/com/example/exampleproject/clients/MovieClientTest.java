package com.example.exampleproject.clients;

import com.example.exampleproject.clients.models.movies.GenreListResponse;
import com.example.exampleproject.clients.models.movies.GenreResponse;
import com.example.exampleproject.clients.models.movies.MovieDetailsResponse;
import com.example.exampleproject.clients.models.movies.MoviePagedResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for class {@link MovieClient}
 */
@Tag(value = "MovieClient_Tests")
@DisplayName("MovieClient Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MovieClientTest {

    private MockWebServer mockWebServer;

    private static final String LIST_GENRES = "listGenres";
    private static final String DISCOVER_BY_GENRE = "discoverByGenre";
    private static final String SEARCH_BY_NAME = "searchByName";
    private static final String GET_DETAILS = "getDetails";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private MovieClient buildClientAgainstMockServer() {
        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
        return new MovieClient(webClient);
    }

    private static ExchangeFunction buildListGenresExchangeFunction(String body) {
        return request -> {
            // basic request assertions following project style
            assertEquals(HttpMethod.GET, request.method());
            assertEquals("/genre/movie/list", request.url().getPath());
            return Mono.just(ClientResponse.create(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .build());
        };
    }

    @Order(1)
    @Test
    @Tag(value = LIST_GENRES)
    @DisplayName(LIST_GENRES + " - When server returns genres then parse and return")
    void testListGenres_WithMockWebServer_ShouldReturnParsedGenreListAndCorrectRequest() throws InterruptedException {
        // Arrange
        String body = """
{
  "genres": [ { "id": 28, "name": "Action" }, { "id": 35, "name": "Comedy" } ]
}
""";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(body));

        MovieClient client = buildClientAgainstMockServer();

        // Act
        GenreListResponse response = client.listGenres();

        // Assert - response mapping
        assertNotNull(response);
        assertNotNull(response.getGenres());
        assertEquals(2, response.getGenres().size());
        assertEquals(new GenreResponse(28, "Action"), response.getGenres().get(0));
        assertEquals(new GenreResponse(35, "Comedy"), response.getGenres().get(1));

        // Assert - request details
        RecordedRequest recorded = mockWebServer.takeRequest();
        assertEquals("GET", recorded.getMethod());
        assertEquals("/genre/movie/list", recorded.getPath());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recorded.getHeader("Accept"));
    }

    @Order(2)
    @Test
    @Tag(value = DISCOVER_BY_GENRE)
    @DisplayName(DISCOVER_BY_GENRE + " - Should send query params and parse page response")
    void testDiscoverByGenre_WithMockWebServer_ShouldIncludeQueryParamsAndReturnPage() throws InterruptedException {
        // Arrange
        String body = """
{
  "page": 3,
  "results": [ { "id": 100 } ],
  "total_pages": 10,
  "total_results": 200
}
""";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(body));

        MovieClient client = buildClientAgainstMockServer();

        // Act
        MoviePagedResponse response = client.discoverByGenre("12", 3);

        // Assert - mapping
        assertNotNull(response);
        assertEquals(3, response.getPage());
        assertEquals(10, response.getTotalPages());
        assertEquals(200, response.getTotalResults());
        assertNotNull(response.getResults());
        assertEquals(1, response.getResults().size());
        assertEquals(100, response.getResults().getFirst().getId());

        // Assert - request details
        RecordedRequest recorded = mockWebServer.takeRequest();
        assertEquals("GET", recorded.getMethod());
        String path = recorded.getPath();
        assertNotNull(path);
        assertTrue(path.startsWith("/discover/movie"));
        assertTrue(path.contains("with_genres=12"));
        assertTrue(path.contains("page=3"));
    }

    @Order(3)
    @Test
    @Tag(value = SEARCH_BY_NAME)
    @DisplayName(SEARCH_BY_NAME + " - Should send query params and parse page response")
    void testSearchByName_WithMockWebServer_ShouldIncludeQueryParamsAndReturnPage() throws InterruptedException {
        // Arrange
        String body = """
{
  "page": 1,
  "results": [ { "id": 501 } ],
  "total_pages": 5,
  "total_results": 5
}
""";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(body));

        MovieClient client = buildClientAgainstMockServer();

        // Act
        MoviePagedResponse response = client.searchByName("the%20bat", 1);

        // Assert - mapping
        assertNotNull(response);
        assertEquals(1, response.getPage());
        assertEquals(5, response.getTotalPages());
        assertEquals(5, response.getTotalResults());
        assertNotNull(response.getResults());
        assertEquals(1, response.getResults().size());
        assertEquals(501, response.getResults().getFirst().getId());

        // Assert - request details
        RecordedRequest recorded = mockWebServer.takeRequest();
        assertEquals("GET", recorded.getMethod());
        String path = recorded.getPath();
        assertNotNull(path);
        assertTrue(path.startsWith("/search/movie"));
        assertTrue(path.contains("query=the%2520bat") 
                || path.contains("query=the+bat") 
                || path.contains("query=the%20bat"));
        assertTrue(path.contains("page=1"));
    }

    @Order(4)
    @Test
    @Tag(value = GET_DETAILS)
    @DisplayName(GET_DETAILS + " - Should send movie_id and parse details")
    void testGetDetails_WithMockWebServer_ShouldIncludeQueryParamAndReturnDetails() throws InterruptedException {
        // Arrange
        String body = """
{
  "id": 999,
  "title": "Interstellar",
  "vote_average": 8.6,
  "vote_count": 1000
}
""";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(body));

        MovieClient client = buildClientAgainstMockServer();

        // Act
        MovieDetailsResponse response = client.getDetails("tt999");

        // Assert - mapping
        assertNotNull(response);
        assertEquals(999, response.getId());
        assertEquals("Interstellar", response.getTitle());
        assertEquals(8.6, response.getVoteAverage());
        assertEquals(1000, response.getVoteCount());

        // Assert - request details
        RecordedRequest recorded = mockWebServer.takeRequest();
        assertEquals("GET", recorded.getMethod());
        String path = recorded.getPath();
        assertNotNull(path);
        assertTrue(path.startsWith("/movies/getdetails"));
        assertTrue(path.contains("movie_id=tt999"));
    }

    @Order(5)
    @Test
    @Tag(value = LIST_GENRES)
    @DisplayName(LIST_GENRES + " - When server returns 500 then WebClientResponseException is thrown")
    void testListGenres_ServerError_ShouldThrowWebClientResponseException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                .setBody("Server error"));

        MovieClient client = buildClientAgainstMockServer();

        // Act & Assert
        var ex = assertThrows(WebClientResponseException.class, client::listGenres);
        assertEquals(500, ex.getStatusCode().value());
        String responseBody = new String(ex.getResponseBodyAsByteArray(), StandardCharsets.UTF_8);
        assertTrue(responseBody.contains("Server error"));
    }

    @Order(6)
    @Test
    @Tag(value = LIST_GENRES)
    @DisplayName(LIST_GENRES + " - Using WebClient with mocked ExchangeFunction should return mapped response")
    void testListGenres_WithMockedExchangeFunction_ShouldReturnResponse() {
        // Arrange - mock ExchangeFunction to avoid real HTTP and fluent chain mocks
        String body = """
{
  "genres": [ { "id": 1, "name": "Drama" } ]
}
""";
        ExchangeFunction exchangeFunction = buildListGenresExchangeFunction(body);

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost")
                .exchangeFunction(exchangeFunction)
                .build();

        MovieClient client = new MovieClient(webClient);

        // Act
        GenreListResponse actual = client.listGenres();

        // Assert
        assertNotNull(actual);
        assertEquals(1, actual.getGenres().size());
        assertEquals(new GenreResponse(1, "Drama"), actual.getGenres().getFirst());
    }
}
