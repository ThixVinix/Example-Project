package com.example.exampleproject.configs.exceptions.handler.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * <h1>{@link WebClientMessageExtractorHelper}</h1>
 *
 * <p>Utility class responsible for extracting error messages from {@link WebClientResponseException}
 * response bodies. This class parses JSON responses and attempts to locate meaningful error messages
 * from various common field names used in different API responses.</p>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Parses JSON response bodies from WebClient exceptions</li>
 *   <li>Searches for error messages in multiple common field names</li>
 *   <li>Handles nested JSON structures (objects and arrays)</li>
 *   <li>Provides fallback mechanisms when messages cannot be extracted</li>
 * </ul>
 *
 * <h2>Implementation</h2>
 * <p>This class is designed as a <strong>utility class</strong>, which means:</p>
 * <ul>
 *   <li>All methods are <code>static</code>.</li>
 *   <li>It includes a private constructor to prevent instantiation.</li>
 * </ul>
 */
@Slf4j
public class WebClientMessageExtractorHelper {

    private WebClientMessageExtractorHelper() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    private static final String WEB_CLIENT_FIELD_FIELD_ERRORS = "fieldErrors";
    private static final String WEB_CLIENT_FIELD_ERROR_FIELDS = "errorFields";
    private static final String WEB_CLIENT_FIELD_MESSAGES = "messages";
    private static final String WEB_CLIENT_FIELD_MESSAGE = "message";
    private static final String WEB_CLIENT_FIELD_MSG = "msg";
    private static final String WEB_CLIENT_FIELD_MENSAGEM = "mensagem";
    private static final String WEB_CLIENT_FIELD_MENSAGENS = "mensagens";
    private static final String WEB_CLIENT_FIELD_DEFAULT_MESSAGE = "defaultMessage";
    private static final String WEB_CLIENT_FIELD_MESSAGE_DETAIL = "messageDetail";
    private static final String WEB_CLIENT_FIELD_DETAILED_MESSAGE = "detailedMessage";
    private static final String WEB_CLIENT_FIELD_MESSAGE_DETAILS_UNDERSCORE = "message_details";
    private static final String WEB_CLIENT_FIELD_DETAIL = "detail";
    private static final String WEB_CLIENT_FIELD_DETAILS = "details";
    private static final String WEB_CLIENT_FIELD_DESCRIPTION = "description";
    private static final String WEB_CLIENT_FIELD_DESCRIPTIONS = "descriptions";
    private static final String WEB_CLIENT_FIELD_REASON = "reason";
    private static final String WEB_CLIENT_FIELD_REASONS = "reasons";
    private static final String WEB_CLIENT_FIELD_CAUSE = "cause";
    private static final String WEB_CLIENT_FIELD_CAUSES = "causes";
    private static final String WEB_CLIENT_FIELD_HINT = "hint";
    private static final String WEB_CLIENT_FIELD_HINTS = "hints";
    private static final String WEB_CLIENT_FIELD_ERROR_DESCRIPTION = "error_description";
    private static final String WEB_CLIENT_FIELD_ERROR_MESSAGE_UNDERSCORE = "error_message";
    private static final String WEB_CLIENT_FIELD_ERROR_MESSAGE = "errorMessage";
    private static final String WEB_CLIENT_FIELD_ERROR_MESSAGES = "errorMessages";
    private static final String WEB_CLIENT_FIELD_ERRO = "erro";
    private static final String WEB_CLIENT_FIELD_ERROS = "erros";
    private static final String WEB_CLIENT_FIELD_ERRORS = "errors";
    private static final String WEB_CLIENT_FIELD_ERROR = "error";

    private static final List<String> MESSAGE_FIELD_CANDIDATES_LIST = List.of(
            WEB_CLIENT_FIELD_FIELD_ERRORS,
            WEB_CLIENT_FIELD_ERROR_FIELDS,
            WEB_CLIENT_FIELD_MESSAGES,
            WEB_CLIENT_FIELD_MESSAGE,
            WEB_CLIENT_FIELD_MSG,
            WEB_CLIENT_FIELD_MENSAGEM,
            WEB_CLIENT_FIELD_MENSAGENS,
            WEB_CLIENT_FIELD_DEFAULT_MESSAGE,
            WEB_CLIENT_FIELD_MESSAGE_DETAIL,
            WEB_CLIENT_FIELD_DETAILED_MESSAGE,
            WEB_CLIENT_FIELD_MESSAGE_DETAILS_UNDERSCORE,
            WEB_CLIENT_FIELD_DETAIL,
            WEB_CLIENT_FIELD_DETAILS,
            WEB_CLIENT_FIELD_DESCRIPTION,
            WEB_CLIENT_FIELD_DESCRIPTIONS,
            WEB_CLIENT_FIELD_REASON,
            WEB_CLIENT_FIELD_REASONS,
            WEB_CLIENT_FIELD_CAUSE,
            WEB_CLIENT_FIELD_CAUSES,
            WEB_CLIENT_FIELD_HINT,
            WEB_CLIENT_FIELD_HINTS,
            WEB_CLIENT_FIELD_ERROR_DESCRIPTION,
            WEB_CLIENT_FIELD_ERROR_MESSAGE_UNDERSCORE,
            WEB_CLIENT_FIELD_ERROR_MESSAGE,
            WEB_CLIENT_FIELD_ERROR_MESSAGES,
            WEB_CLIENT_FIELD_ERRO,
            WEB_CLIENT_FIELD_ERROS,
            WEB_CLIENT_FIELD_ERRORS,
            WEB_CLIENT_FIELD_ERROR
    );

    /**
     * Extracts an error message from a {@link WebClientResponseException} by parsing its response body.
     * This method attempts to locate meaningful error messages from various common field names.
     *
     * @param webClientEx The WebClientResponseException containing the response body to parse
     * @return An Optional containing the extracted message or empty if extraction fails
     */
    public static Optional<String> extractMessageFromWebClientResponseException(
            final WebClientResponseException webClientEx) {
        try {
            String bodyString =
                    Optional.of(webClientEx.getResponseBodyAsString())
                            .map(String::trim)
                            .orElse(StringUtils.EMPTY);

            if (bodyString.isEmpty()) {
                log.debug("WebClientResponseException response body missing or empty");
                return Optional.empty();
            }

            JsonNode rootNode = parseJson(bodyString);
            return findFirstFieldIn(rootNode);
        } catch (Exception e) {
            log.warn("Failed to extract message from WebClientResponseException body: {}", e.getMessage(), e);
            return Optional.empty();
        }
    }

    private static JsonNode parseJson(final String json) throws JsonProcessingException {
        return new ObjectMapper().readTree(json);
    }

    private static Optional<String> findFirstFieldIn(JsonNode rootNode) {
        if (isNullNode(rootNode)) {
            return Optional.empty();
        }

        Map<String, String> foundCandidates = collectCandidateValues(rootNode);

        return MESSAGE_FIELD_CANDIDATES_LIST.stream()
                .filter(foundCandidates::containsKey)
                .map(foundCandidates::get)
                .findFirst();
    }

    private static Map<String, String> collectCandidateValues(JsonNode rootNode) {
        Map<String, String> foundCandidates = new HashMap<>();
        Set<String> targetFields = new HashSet<>(MESSAGE_FIELD_CANDIDATES_LIST);

        Queue<JsonNode> queue = new LinkedList<>();
        queue.add(rootNode);

        while (!queue.isEmpty()) {
            JsonNode currentNode = queue.poll();

            if (currentNode.isObject()) {
                extractFromObjectFields(currentNode, targetFields, foundCandidates);
            }

            if (currentNode.isContainerNode()) {
                currentNode.elements().forEachRemaining(queue::add);
            }
        }
        return foundCandidates;
    }

    private static void extractFromObjectFields(JsonNode objectNode,
                                                Set<String> targetFields,
                                                Map<String, String> foundCandidates) {
        Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            String key = field.getKey();
            if (targetFields.contains(key) && !foundCandidates.containsKey(key)) {
                extractIfHasNonBlank(objectNode, key).ifPresent(val -> foundCandidates.put(key, val));
            }
        }
    }

    private static boolean isNullNode(JsonNode node) {
        return node == null || node.isNull();
    }

    private static Optional<String> extractIfHasNonBlank(JsonNode node, String fieldName) {
        JsonNode valueNode = node.get(fieldName);

        if (isNull(valueNode) || valueNode.isNull()) {
            return Optional.empty();
        }

        if (valueNode.isContainerNode()) {
            return valueNode.isArray()
                    ? extractFromArray(valueNode, fieldName)
                    : extractFromObject(valueNode, fieldName);
        }

        return Optional.of(valueNode.asText(StringUtils.EMPTY).trim())
                .filter((String text) -> !text.isEmpty())
                .map((String text) -> {
                    log.debug("Recursively extracted '{}' field from WebClientResponseException", fieldName);
                    return text;
                });
    }

    private static Optional<String> extractFromArray(JsonNode node, String fieldName) {
        String joined = StreamSupport.stream(node.spliterator(), false)
                .filter(element -> nonNull(element) && !element.isNull() && element.isValueNode())
                .map(element -> element.asText(StringUtils.EMPTY).trim())
                .filter(text -> !text.isEmpty())
                .collect(Collectors.joining("; "));

        if (!joined.isEmpty()) {
            log.debug("Recursively extracted '{}' field (array) from WebClientResponseException", fieldName);
            return Optional.of(joined);
        }
        return Optional.empty();
    }

    private static Optional<String> extractFromObject(JsonNode node, String fieldName) {
        String joined = StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(node.fields(), Spliterator.ORDERED), false)
                .map((Map.Entry<String, JsonNode> entry) -> {
                    JsonNode objectNode = entry.getValue();
                    if (isNull(objectNode) || objectNode.isNull() || !objectNode.isValueNode()) {
                        return StringUtils.EMPTY;
                    }
                    String key = Optional.ofNullable(entry.getKey()).map(String::trim).orElse(StringUtils.EMPTY);
                    String text = objectNode.asText(StringUtils.EMPTY).trim();
                    return ((!key.isEmpty() && !text.isEmpty()) ? (key + ": " + text) : StringUtils.EMPTY);
                })
                .filter((String text) -> !text.isEmpty())
                .collect(Collectors.joining("; "));

        if (!joined.isEmpty()) {
            log.debug("Recursively extracted '{}' field (object) from WebClientResponseException", fieldName);
            return Optional.of(joined);
        }
        return Optional.empty();
    }
}
