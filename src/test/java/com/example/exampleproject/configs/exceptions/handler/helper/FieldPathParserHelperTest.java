package com.example.exampleproject.configs.exceptions.handler.helper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Method test for {@link FieldPathParserHelper}
 */
@Tag(value = "FieldPathParserHelper_Tests")
@DisplayName("FieldPathParserHelper Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
class FieldPathParserHelperTest {

    private static final String PARSE = "parse";

    /**
     * Method test for {@link FieldPathParserHelper#parse(List)}
     */
    @Order(1)
    @Tag(value = PARSE)
    @DisplayName(PARSE + " Given simple path, then should parse correctly")
    @Test
    void parse_WhenSimplePath_ThenShouldParseCorrectly() {
        // Arrange
        String path = "field";
        FieldPathParserHelper helper = new FieldPathParserHelper(path);
        List<String> segments = new ArrayList<>();

        // Act
        boolean result = helper.parse(segments);

        // Assert
        assertTrue(result, "Should parse successfully");
        assertEquals(1, segments.size(), "Should have 1 segment");
        assertEquals("field", segments.get(0), "Segment should match");
    }

    /**
     * Method test for {@link FieldPathParserHelper#parse(List)}
     */
    @Order(2)
    @Tag(value = PARSE)
    @DisplayName(PARSE + " Given dot separated path, then should parse correctly")
    @Test
    void parse_WhenDotSeparatedPath_ThenShouldParseCorrectly() {
        // Arrange
        String path = "parent.child.subChild";
        FieldPathParserHelper helper = new FieldPathParserHelper(path);
        List<String> segments = new ArrayList<>();

        // Act
        boolean result = helper.parse(segments);

        // Assert
        assertTrue(result, "Should parse successfully");
        assertEquals(3, segments.size(), "Should have 3 segments");
        assertEquals("parent", segments.get(0), "First segment should match");
        assertEquals("child", segments.get(1), "Second segment should match");
        assertEquals("subChild", segments.get(2), "Third segment should match");
    }

    /**
     * Method test for {@link FieldPathParserHelper#parse(List)}
     */
    @Order(3)
    @Tag(value = PARSE)
    @DisplayName(PARSE + " Given path with brackets, then should ignore dots inside brackets")
    @Test
    void parse_WhenPathWithBrackets_ThenShouldIgnoreDotsInsideBrackets() {
        // Arrange
        String path = "parent[child.name].field";
        FieldPathParserHelper helper = new FieldPathParserHelper(path);
        List<String> segments = new ArrayList<>();

        // Act
        boolean result = helper.parse(segments);

        // Assert
        assertTrue(result, "Should parse successfully");
        assertEquals(2, segments.size(), "Should have 2 segments");
        assertEquals("parent[child.name]", segments.get(0), "First segment should include brackets content");
        assertEquals("field", segments.get(1), "Second segment should match");
    }

    /**
     * Method test for {@link FieldPathParserHelper#parse(List)}
     */
    @Order(4)
    @Tag(value = PARSE)
    @DisplayName(PARSE + " Given path with nested brackets, then should parse correctly")
    @Test
    void parse_WhenNestedBrackets_ThenShouldParseCorrectly() {
        // Arrange
        String path = "parent[index[subIndex]].field";
        FieldPathParserHelper helper = new FieldPathParserHelper(path);
        List<String> segments = new ArrayList<>();

        // Act
        boolean result = helper.parse(segments);

        // Assert
        assertTrue(result, "Should parse successfully");
        assertEquals(2, segments.size(), "Should have 2 segments");
        assertEquals("parent[index[subIndex]]", segments.get(0), "First segment should include nested brackets");
        assertEquals("field", segments.get(1), "Second segment should match");
    }

    /**
     * Method test for {@link FieldPathParserHelper#parse(List)}
     */
    @Order(5)
    @Tag(value = PARSE)
    @DisplayName(PARSE + " Given unbalanced opening brackets, then should return false")
    @Test
    void parse_WhenUnbalancedOpeningBrackets_ThenShouldReturnFalse() {
        // Arrange
        String path = "field[unbalanced";
        FieldPathParserHelper helper = new FieldPathParserHelper(path);
        List<String> segments = new ArrayList<>();

        // Act
        boolean result = helper.parse(segments);

        // Assert
        assertFalse(result, "Should fail due to unbalanced opening bracket");
    }

    /**
     * Method test for {@link FieldPathParserHelper#parse(List)}
     */
    @Order(6)
    @Tag(value = PARSE)
    @DisplayName(PARSE + " Given unbalanced closing brackets, then should return false")
    @Test
    void parse_WhenUnbalancedClosingBrackets_ThenShouldReturnFalse() {
        // Arrange
        String path = "field]unbalanced";
        FieldPathParserHelper helper = new FieldPathParserHelper(path);
        List<String> segments = new ArrayList<>();

        // Act
        boolean result = helper.parse(segments);

        // Assert
        assertFalse(result, "Should fail due to unbalanced closing bracket");
    }

    /**
     * Method test for {@link FieldPathParserHelper#parse(List)}
     */
    @Order(7)
    @Tag(value = PARSE)
    @DisplayName(PARSE + " Given empty path, then should return true with no segments")
    @Test
    void parse_WhenEmptyPath_ThenShouldReturnTrueWithNoSegments() {
        // Arrange
        String path = "";
        FieldPathParserHelper helper = new FieldPathParserHelper(path);
        List<String> segments = new ArrayList<>();

        // Act
        boolean result = helper.parse(segments);

        // Assert
        assertTrue(result, "Should parse successfully");
        assertTrue(segments.isEmpty(), "Segments list should be empty");
    }
}
