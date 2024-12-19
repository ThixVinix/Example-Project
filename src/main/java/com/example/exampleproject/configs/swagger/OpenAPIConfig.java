package com.example.exampleproject.configs.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Profile("!prd")
@Configuration
public class OpenAPIConfig {

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

    @Bean
    public OpenApiCustomizer globalHeaderCustomizer() {
        return openApi -> openApi.getPaths().forEach((_, pathItem) ->
                pathItem.readOperations().forEach(operation -> {

                    final String HEADER_LOCATION = "header";
                    final String PORTUGUESE_BRAZIL_LANGUAGE_TAG = "pt-BR";
                    final String ENGLISH_LANGUAGE_TAG = "en";

                    List<Locale> supportedLanguages = List.of(
                            Locale.forLanguageTag(PORTUGUESE_BRAZIL_LANGUAGE_TAG),
                            Locale.forLanguageTag(ENGLISH_LANGUAGE_TAG)
                    );

                    String supportedLanguagesString = supportedLanguages.stream()
                            .map(Locale::toLanguageTag)
                            .collect(Collectors.joining(", "));

                    Parameter acceptLanguageHeader = new Parameter()
                            .in(HEADER_LOCATION)
                            .name(HttpHeaders.ACCEPT_LANGUAGE)
                            .description("Preferred language for the service response (Optional). " +
                                    "Currently supported languages: " + supportedLanguagesString)
                            .required(Boolean.FALSE)
                            .schema(new Schema<String>().example(PORTUGUESE_BRAZIL_LANGUAGE_TAG));

                    operation.addParametersItem(acceptLanguageHeader);
                }));
    }

}
