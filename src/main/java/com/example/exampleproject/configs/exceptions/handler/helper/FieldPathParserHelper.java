package com.example.exampleproject.configs.exceptions.handler.helper;

import java.util.List;

/**
 * Helper class to parse field paths with support for nested structures and array/collection indexing.
 * Handles bracket notation and dot-separated field names.
 */
public class FieldPathParserHelper {
    private final String fieldPath;
    private int segmentStart = 0;
    private int bracketDepth = 0;

    FieldPathParserHelper(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public boolean parse(List<String> segments) {
        for (int i = 0; i < fieldPath.length(); i++) {
            if (!processCharacter(fieldPath.charAt(i), i, segments)) {
                return false;
            }
        }

        if (bracketDepth != 0) {
            return false;
        }

        addFinalSegment(segments);
        return true;
    }

    private boolean processCharacter(char currentChar, int position, List<String> segments) {
        if (currentChar == '[') {
            bracketDepth++;
            return true;
        }

        if (currentChar == ']') {
            bracketDepth--;
            return bracketDepth >= 0;
        }

        if (currentChar == '.' && bracketDepth == 0) {
            segments.add(fieldPath.substring(segmentStart, position));
            segmentStart = position + 1;
        }

        return true;
    }

    private void addFinalSegment(List<String> segments) {
        if (segmentStart < fieldPath.length()) {
            segments.add(fieldPath.substring(segmentStart));
        }
    }
}
