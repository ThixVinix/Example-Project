package com.example.exampleproject;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Tag("ExampleProjectApplication_Tests")
@DisplayName("ExampleProjectApplication Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExampleProjectApplicationTests {

    @Test
    @Order(1)
    @Tag("main_method")
    @DisplayName("Test main method")
    void contextLoads() {
        String[] args = {};
        Assertions.assertDoesNotThrow(() -> ExampleProjectApplication.main(args));
    }

}