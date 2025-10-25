package com.example.exampleproject.configs.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "rapid-api")
public class RapidApiProperties {
    private String host;
    private String baseUrl;
    private String key;
}
