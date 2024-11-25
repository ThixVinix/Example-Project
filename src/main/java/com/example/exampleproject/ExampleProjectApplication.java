package com.example.exampleproject;

import com.example.exampleproject.configs.banners.ColoredBanner;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ExampleProjectApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExampleProjectApplication.class);
        configureSpringApplication(app).run(args);
    }

    private static SpringApplication configureSpringApplication(SpringApplication app) {
        app.setBannerMode(Banner.Mode.CONSOLE);
        app.setBanner(new ColoredBanner());
        return app;
    }

}
