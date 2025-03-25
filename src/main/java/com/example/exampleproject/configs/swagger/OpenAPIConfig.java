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

                    String supportedLanguagesHtmlList = supportedLanguages.stream()
                            .map(locale -> String.format("<li>%s</li>", locale.toLanguageTag()))
                            .collect(Collectors.joining());

                    String description = String.format(
                            "<p>Idioma preferido para a resposta do serviço (opcional).</p>" +
                                    "<p>Caso não seja informado, o idioma padrão utilizado será " +
                                    "<strong>%s</strong>.</p>" +
                                    "<p>Idiomas atualmente suportados:</p>" +
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

}
