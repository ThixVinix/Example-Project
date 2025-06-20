package com.example.exampleproject.configs.swagger;

import com.example.exampleproject.configs.exceptions.ErrorMultipleResponse;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.example.exampleproject.configs.swagger.SwaggerHttpDescriptionsHelper.*;
import static com.example.exampleproject.configs.swagger.SwaggerBodyExamplesHelper.*;

@Profile("!prd")
@Configuration
public class OpenAPIConfig {

    private static final String HEADER_LOCATION = "header";
    private static final String PORTUGUESE_BRAZIL_LANGUAGE_TAG = "pt-BR";
    private static final String ENGLISH_LANGUAGE_TAG = "en";

    @Value("${api.title}")
    private String apiTitle;

    @Value("${api.version}")
    private String apiVersion;

    @Value("${api.description}")
    private String apiDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(apiTitle)
                        .version(apiVersion)
                        .description(apiDescription));
    }

    /**
     * Customizes the global behavior of the OpenAPI specification by adding
     * predefined HTTP headers, global API responses, and component schemas.
     * <p>
     * This method creates and returns an {@link OpenApiCustomizer} implementation
     * that enriches the OpenAPI documentation with metadata applicable across all APIs.
     * The customizer ensures the inclusion of:
     * <ul>
     *   <li>Global HTTP headers for language preference.</li>
     *   <li>Standardized global API error responses.</li>
     *   <li>Predefined schemas within the components section.</li>
     * </ul>
     *
     * @return an {@link OpenApiCustomizer} that applies global alterations
     *         to the OpenAPI specification.
     */
    @Bean
    public OpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            addGlobalHttpHeaders(openApi);
            addGlobalApiResponses(openApi);
            addSchemasInComponents(openApi);
        };
    }

    private void addGlobalHttpHeaders(OpenAPI openApi) {
        openApi.getPaths().forEach((_, pathItem) ->
            pathItem.readOperations().forEach(operation -> {

                List<Locale> supportedLanguages = List.of(
                        Locale.forLanguageTag(PORTUGUESE_BRAZIL_LANGUAGE_TAG),
                        Locale.forLanguageTag(ENGLISH_LANGUAGE_TAG)
                );

                String supportedLanguagesHtmlList = supportedLanguages.stream()
                        .map(locale -> String.format("<li>%s</li>", locale.toLanguageTag()))
                        .collect(Collectors.joining());

                String description = String.format(
                        "<p>Preferred language for service response (optional).</p>" +
                                "<p>If not informed, the default language used will be " +
                                "<strong>%s</strong>.</p>" +
                                "<p>Currently supported languages:</p>" +
                                "<ul>%s</ul>", PORTUGUESE_BRAZIL_LANGUAGE_TAG, supportedLanguagesHtmlList);

                Parameter acceptLanguageHeader = new Parameter()
                        .in(HEADER_LOCATION)
                        .name(HttpHeaders.ACCEPT_LANGUAGE)
                        .description(description)
                        .required(Boolean.FALSE)
                        .schema(new Schema<String>().example(PORTUGUESE_BRAZIL_LANGUAGE_TAG));

                operation.addParametersItem(acceptLanguageHeader);
            }));
    }


    private void addGlobalApiResponses(OpenAPI openApi) {
        if (openApi.getComponents() == null) {
            openApi.setComponents(new Components());
        }

        // Add global response components
        openApi.getPaths().forEach((path, pathItem) ->
                pathItem.readOperations().forEach(operation -> {
            if (operation.getResponses() == null) {
                operation.setResponses(new ApiResponses());
            }

            // 404 Not Found
            operation.getResponses().addApiResponse("404", new ApiResponse()
                    .description(NOT_FOUND_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 404 Not Found Example", new Example()
                                                    .summary("English: Example of a 404 error response.")
                                                    .value(NOT_FOUND_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 404 Recurso Não Encontrado",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 404.")
                                                    .value(NOT_FOUND_EXAMPLE_PT)))));

            // 405 Method Not Allowed
            operation.getResponses().addApiResponse("405", new ApiResponse()
                    .description(METHOD_NOT_ALLOWED_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 405 Method Not Allowed",
                                                    new Example()
                                                    .summary("English: Example of a 405 error response when " +
                                                            "using an unsupported method.")
                                                    .value(METHOD_NOT_ALLOWED_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 405 Método Não Permitido",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 405 ao usar um método não suportado.")
                                                    .value(METHOD_NOT_ALLOWED_EXAMPLE_PT)))));

            // 400 Bad Request
            operation.getResponses().addApiResponse("400", new ApiResponse()
                    .description(BAD_REQUEST_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<>()
                                                    .oneOf(List.of(
                                                            new Schema<ErrorMultipleResponse>()
                                                                    .$ref("#/components/schemas/ErrorMultipleResponse"),
                                                            new Schema<ErrorSingleResponse>()
                                                                    .$ref("#/components/schemas/ErrorSingleResponse"))))
                                            .addExamples("English - 400 Bad Request (Multiple Errors)",
                                                    new Example()
                                                    .summary("English: Example of a 400 error response with " +
                                                            "multiple validation errors.")
                                                    .value(BAD_REQUEST_MULTIPLE_ERRORS_EXAMPLE_EN))
                                            .addExamples("English - 400 Bad Request (Single Error)",
                                                    new Example()
                                                    .summary("English: Example of a 400 error response with a " +
                                                            "single error message.")
                                                    .value(BAD_REQUEST_SINGLE_ERROR_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 400 Requisição Inválida " +
                                                    "(Múltiplos Erros)",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 400 com múltiplos erros de validação.")
                                                    .value(BAD_REQUEST_MULTIPLE_ERRORS_EXAMPLE_PT))
                                            .addExamples("Brazilian Portuguese - 400 Requisição Inválida " +
                                                    "(Erro Único)",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 400 com uma única mensagem de erro.")
                                                    .value(BAD_REQUEST_SINGLE_ERROR_EXAMPLE_PT)))));

            // 401 Unauthorized
            operation.getResponses().addApiResponse("401", new ApiResponse()
                    .description(UNAUTHORIZED_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 401 Unauthorized",
                                                    new Example()
                                                    .summary("English: Example of a 401 error response caused by " +
                                                            "missing or invalid authentication credentials.")
                                                    .value(UNAUTHORIZED_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 401 Não Autorizado",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 401 causada por credenciais de autenticação " +
                                                            "ausentes ou inválidas.")
                                                    .value(UNAUTHORIZED_EXAMPLE_PT)))));

            // 403 Forbidden
            operation.getResponses().addApiResponse("403", new ApiResponse()
                    .description(FORBIDDEN_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 403 Forbidden", new Example()
                                                    .summary("English: Example of a 403 error response when " +
                                                            "access is denied.")
                                                    .value(FORBIDDEN_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 403 Proibido", new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 403 quando o acesso é negado.")
                                                    .value(FORBIDDEN_EXAMPLE_PT)))));

            // 409 Conflict
            operation.getResponses().addApiResponse("409", new ApiResponse()
                    .description(CONFLICT_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 409 Conflict",
                                                    new Example()
                                                    .summary("English: Example of a 409 error response caused by " +
                                                            "a duplicate entry.")
                                                    .value(CONFLICT_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 409 Conflito",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 409 causada por uma entrada duplicada.")
                                                    .value(CONFLICT_EXAMPLE_PT)))));

            // 408 Request Timeout
            operation.getResponses().addApiResponse("408", new ApiResponse()
                    .description(REQUEST_TIMEOUT_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 408 Request Timeout",
                                                    new Example()
                                                    .summary("English: Example of a 408 error response caused by " +
                                                            "a timeout.")
                                                    .value(REQUEST_TIMEOUT_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 408 Tempo Limite da " +
                                                    "Solicitação",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 408 causada por um tempo limite.")
                                                    .value(REQUEST_TIMEOUT_EXAMPLE_PT)))));

            // 503 Service Unavailable
            operation.getResponses().addApiResponse("503", new ApiResponse()
                    .description(SERVICE_UNAVAILABLE_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 503 Service Unavailable",
                                                    new Example()
                                                    .summary("English: Example of a 503 error response caused by " +
                                                            "a service unavailability.")
                                                    .value(SERVICE_UNAVAILABLE_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 503 Serviço Indisponível",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 503 causada por indisponibilidade do serviço.")
                                                    .value(SERVICE_UNAVAILABLE_EXAMPLE_PT)))));

            // 406 Not Acceptable
            operation.getResponses().addApiResponse("406", new ApiResponse()
                    .description(NOT_ACCEPTABLE_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 406 Not Acceptable", new Example()
                                                    .summary("English: Example of a 406 error response caused by " +
                                                            "unsupported 'Accept' header value.")
                                                    .value(NOT_ACCEPTABLE_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 406 Não Aceitável",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 406 causada por um valor de cabeçalho 'Accept' " +
                                                            "não suportado.")
                                                    .value(NOT_ACCEPTABLE_EXAMPLE_PT)))));

            // 415 Unsupported Media Type
            operation.getResponses().addApiResponse("415", new ApiResponse()
                    .description(UNSUPPORTED_MEDIA_TYPE_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 415 Unsupported Media Type", new Example()
                                                    .summary("English: Example of a 415 error response caused by " +
                                                            "an unsupported 'Content-Type' header.")
                                                    .value(UNSUPPORTED_MEDIA_TYPE_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 415 Tipo de Mídia Não " +
                                                    "Suportado",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 415 causada por um cabeçalho 'Content-Type' " +
                                                            "não suportado.")
                                                    .value(UNSUPPORTED_MEDIA_TYPE_EXAMPLE_PT)))));

            // 413 Payload Too Large
            operation.getResponses().addApiResponse("413", new ApiResponse()
                    .description(PAYLOAD_TOO_LARGE_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 413 Payload Too Large", new Example()
                                                    .summary("English: Example of a 413 error response caused by " +
                                                            "an uploaded file that exceeds the allowed size.")
                                                    .value(PAYLOAD_TOO_LARGE_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 413 Carga Muito Grande",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 413 causada por um arquivo carregado que " +
                                                            "excede o tamanho permitido.")
                                                    .value(PAYLOAD_TOO_LARGE_EXAMPLE_PT)))));

            // 500 Internal Server Error
            operation.getResponses().addApiResponse("500", new ApiResponse()
                    .description(INTERNAL_SERVER_ERROR_DESCRIPTION)
                    .content(new Content()
                            .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                    new io.swagger.v3.oas.models.media.MediaType()
                                            .schema(new Schema<ErrorSingleResponse>()
                                                    .$ref("#/components/schemas/ErrorSingleResponse"))
                                            .addExamples("English - 500 Internal Server Error", new Example()
                                                    .summary("English: Example of a 500 error response caused by " +
                                                            "an unexpected server-side failure.")
                                                    .value(INTERNAL_SERVER_ERROR_EXAMPLE_EN))
                                            .addExamples("Brazilian Portuguese - 500 Erro Interno do Servidor",
                                                    new Example()
                                                    .summary("Brazilian Portuguese: Exemplo de uma resposta de " +
                                                            "erro 500 causada por uma falha inesperada do lado " +
                                                            "do servidor.")
                                                    .value(INTERNAL_SERVER_ERROR_EXAMPLE_PT)))));
        }));

    }

    private void addSchemasInComponents(OpenAPI openApi) {
        openApi.getComponents().addSchemas("ErrorSingleResponse", new Schema<ErrorSingleResponse>()
                .type("object")
                .addProperty("timestamp", new Schema<>().type("string").format("date-time"))
                .addProperty("path", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").format("int32"))
                .addProperty("error", new Schema<>().type("string"))
                .addProperty("message", new Schema<>().type("string")));

        openApi.getComponents().addSchemas("ErrorMultipleResponse", new Schema<ErrorMultipleResponse>()
                .type("object")
                .addProperty("timestamp", new Schema<>().type("string").format("date-time"))
                .addProperty("path", new Schema<>().type("string"))
                .addProperty("status", new Schema<>().type("integer").format("int32"))
                .addProperty("error", new Schema<>().type("string"))
                .addProperty("messages", new Schema<>().type("object")
                        .additionalProperties(new Schema<>().type("string"))));
    }

}
