package com.example.exampleproject.clients;

import com.example.exampleproject.clients.models.Address;
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
 * Tests for class {@link ViaCepClient}
 */
@Tag(value = "ViaCepClient_Tests")
@DisplayName("ViaCepClient Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ViaCepClientTest {

    private MockWebServer mockWebServer;

    private static final String SEARCH_ADDRESS_BY_ZIP_CODE = "searchAddressByZipCode";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    private ViaCepClient buildClientAgainstMockServer() {
        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .build();
        return new ViaCepClient(webClient);
    }

    @Order(1)
    @Test
    @Tag(value = SEARCH_ADDRESS_BY_ZIP_CODE)
    @DisplayName(SEARCH_ADDRESS_BY_ZIP_CODE + " - Should GET /{cep}/json/ and map response")
    void testSearchAddressByZipCode_ShouldReturnAddressAndCorrectRequest() throws InterruptedException {
        // Arrange
        String body = """
{
  "cep": "01001-000",
  "logradouro": "Praça da Sé",
  "complemento": "lado ímpar",
  "bairro": "Sé",
  "localidade": "São Paulo",
  "uf": "SP",
  "ibge": "3550308",
  "gia": "1004",
  "ddd": "11"
}
""";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody(body));

        ViaCepClient client = buildClientAgainstMockServer();

        // Act
        Address response = client.searchAddressByZipCode("01001000");

        // Assert mapping
        assertNotNull(response);
        assertEquals("01001-000", response.zipCode());
        assertEquals("Praça da Sé", response.street());
        assertEquals("lado ímpar", response.complement());
        assertEquals("Sé", response.neighborhood());
        assertEquals("São Paulo", response.city());
        assertEquals("SP", response.state());
        assertEquals("3550308", response.ibge());
        assertEquals("1004", response.gia());
        assertEquals("11", response.ddd());

        // Assert request
        RecordedRequest recorded = mockWebServer.takeRequest();
        assertEquals("GET", recorded.getMethod());
        assertEquals("/01001000/json/", recorded.getPath());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, recorded.getHeader("Accept"));
    }

    @Order(2)
    @Test
    @Tag(value = SEARCH_ADDRESS_BY_ZIP_CODE)
    @DisplayName(SEARCH_ADDRESS_BY_ZIP_CODE + " - When server returns 500 then WebClientResponseException is thrown")
    void testSearchAddressByZipCode_ServerError_ShouldThrowWebClientResponseException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .addHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                .setBody("Server error"));

        ViaCepClient client = buildClientAgainstMockServer();

        // Act & Assert
        var ex = assertThrows(WebClientResponseException.class, () -> client.searchAddressByZipCode("99999999"));
        assertEquals(500, ex.getStatusCode().value());
        String responseBody = new String(ex.getResponseBodyAsByteArray(), StandardCharsets.UTF_8);
        assertTrue(responseBody.contains("Server error"));
    }
}
