package com.example.exampleproject.configs.swagger;

/**
 * A utility class that provides predefined example response bodies for various HTTP status codes.
 * These examples are useful for documenting RESTful APIs, primarily for integration with tools
 * like Swagger/OpenAPI. Each example provides localized variants in English and Portuguese
 * (en and pt) and represents typical error responses formatted in JSON structure.
 *
 * <p>Note: This class contains only static members and cannot be instantiated.</p>
 *
 * <h2>Supported HTTP Status Codes and Examples</h2>
 * The class provides the following example response data for the associated status codes:
 * <ul>
 *   <li><b>404 Not Found:</b> Example messages signify that a resource was not found for
 *       the provided identifier.</li>
 *   <li><b>405 Method Not Allowed:</b> Example messages indicate that an HTTP method is
 *       not supported for the targeted resource.</li>
 *   <li><b>400 Bad Request:</b> Examples for both single and multiple validation errors,
 *       highlighting issues with request parameters.</li>
 *   <li><b>401 Unauthorized:</b> Example responses for authentication-related failures.</li>
 *   <li><b>403 Forbidden:</b> Examples illustrating insufficient permissions to access a resource.</li>
 *   <li><b>409 Conflict:</b> Examples demonstrating scenarios where resource conflicts
 *       occur, such as duplicate identifier issues.</li>
 *   <li><b>408 Request Timeout:</b> Examples describing server timeout scenarios while
 *       waiting for a request to complete.</li>
 *   <li><b>503 Service Unavailable:</b> Examples indicating temporary service unavailability.</li>
 *   <li><b>406 Not Acceptable:</b> Examples for situations where the server cannot fulfill
 *       a request based on the requested content type.</li>
 *   <li><b>415 Unsupported Media Type:</b> Examples depict errors caused by unsupported
 *       media types in the request body.</li>
 * </ul>
 *
 * <h2>Utility Nature</h2>
 * This is a final utility class, which:
 * <ul>
 *   <li>Cannot be extended or instantiated.</li>
 *   <li>Contains only publicly accessible static string constants, representing the example responses.</li>
 * </ul>
 *
 * <h2>Applications</h2>
 * The examples provided by this class can serve the following purposes:
 * <ul>
 *   <li>Generating standardized API documentation with illustrative error response examples.</li>
 *   <li>Supporting developers with a clear understanding of HTTP error responses during
 *       API integration and debugging.</li>
 * </ul>
 *
 * <h2>Localized Content</h2>
 * Each example is available in two languages:
 * <ul>
 *   <li><b>en:</b> English (default language).</li>
 *   <li><b>pt:</b> Portuguese (localized error messages).</li>
 * </ul>
 */
public final class SwaggerBodyExamplesHelper {

    private SwaggerBodyExamplesHelper() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    // 404 Not Found examples
    public static final String NOT_FOUND_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T12:00:00",
              "path": "/api/resource/123",
              "status": 404,
              "error": "Not Found",
              "message": "Resource with ID '123' was not found."
            }
        """;

    public static final String NOT_FOUND_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T12:00:00",
              "path": "/api/resource/123",
              "status": 404,
              "error": "Not Found",
              "message": "Recurso com ID '123' não foi encontrado."
            }
        """;

    // 405 Method Not Allowed examples
    public static final String METHOD_NOT_ALLOWED_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T12:00:00",
              "path": "/api/resource",
              "status": 405,
              "error": "Method Not Allowed",
              "message": "POST is not supported for this resource."
            }
        """;

    public static final String METHOD_NOT_ALLOWED_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T12:00:00",
              "path": "/api/resource",
              "status": 405,
              "error": "Method Not Allowed",
              "message": "POST não é suportado para este recurso."
            }
        """;

    // 400 Bad Request examples
    public static final String BAD_REQUEST_MULTIPLE_ERRORS_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T14:00:00",
              "path": "/api/resource",
              "status": 400,
              "error": "Bad Request",
              "messages": {
                "field1": "Field is required.",
                "field2": "It should not be empty."
              }
            }
        """;

    public static final String BAD_REQUEST_SINGLE_ERROR_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T14:00:00",
              "path": "/api/resource",
              "status": 400,
              "error": "Bad Request",
              "message": "Invalid request format."
            }
        """;

    public static final String BAD_REQUEST_MULTIPLE_ERRORS_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T14:00:00",
              "path": "/api/resource",
              "status": 400,
              "error": "Bad Request",
              "messages": {
                "campo1": "Campo é obrigatório.",
                "campo2": "Não deve estar vazio."
              }
            }
        """;

    public static final String BAD_REQUEST_SINGLE_ERROR_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T14:00:00",
              "path": "/api/resource",
              "status": 400,
              "error": "Bad Request",
              "message": "Formato de requisição inválido."
            }
        """;

    // 401 Unauthorized examples
    public static final String UNAUTHORIZED_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T12:00:00",
              "path": "/api/resource",
              "status": 401,
              "error": "Unauthorized",
              "message": "Authentication failed due to missing or invalid credentials."
            }
        """;

    public static final String UNAUTHORIZED_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T12:00:00",
              "path": "/api/resource",
              "status": 401,
              "error": "Unauthorized",
              "message": "Falha na autenticação devido a credenciais ausentes ou inválidas."
            }
        """;

    // 403 Forbidden examples
    public static final String FORBIDDEN_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T15:00:00",
              "path": "/api/protected-resource",
              "status": 403,
              "error": "Forbidden",
              "message": "Access denied. You do not have sufficient permissions to access this resource."
            }
        """;

    public static final String FORBIDDEN_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T15:00:00",
              "path": "/api/protected-resource",
              "status": 403,
              "error": "Forbidden",
              "message": "Acesso negado. Você não tem permissões suficientes para acessar este recurso."
            }
        """;

    // 409 Conflict examples
    public static final String CONFLICT_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T16:00:00",
              "path": "/api/resource",
              "status": 409,
              "error": "Conflict",
              "message": "A resource with the identifier '123' already exists."
            }
        """;

    public static final String CONFLICT_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T16:00:00",
              "path": "/api/resource",
              "status": 409,
              "error": "Conflict",
              "message": "Um recurso com o identificador '123' já existe."
            }
        """;

    // 408 Request Timeout examples
    public static final String REQUEST_TIMEOUT_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T17:00:00",
              "path": "/api/resource",
              "status": 408,
              "error": "Request Timeout",
              "message": "The server timed out waiting for the request to complete."
            }
        """;

    public static final String REQUEST_TIMEOUT_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T17:00:00",
              "path": "/api/resource",
              "status": 408,
              "error": "Request Timeout",
              "message": "O servidor atingiu o tempo limite aguardando a conclusão da solicitação."
            }
        """;

    // 503 Service Unavailable examples
    public static final String SERVICE_UNAVAILABLE_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T18:00:00",
              "path": "/api/resource",
              "status": 503,
              "error": "Service Unavailable",
              "message": "The service is currently unavailable. Please try again later."
            }
        """;

    public static final String SERVICE_UNAVAILABLE_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T18:00:00",
              "path": "/api/resource",
              "status": 503,
              "error": "Service Unavailable",
              "message": "O serviço está temporariamente indisponível. Por favor, tente novamente mais tarde."
            }
        """;

    // 406 Not Acceptable examples
    public static final String NOT_ACCEPTABLE_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T18:00:00",
              "path": "/api/resource",
              "status": 406,
              "error": "Not Acceptable",
              "message": "The server cannot produce a response in the requested format: 'application/xml'."
            }
        """;

    public static final String NOT_ACCEPTABLE_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T18:00:00",
              "path": "/api/resource",
              "status": 406,
              "error": "Not Acceptable",
              "message": "O servidor não pode produzir uma resposta no formato solicitado: 'application/xml'."
            }
        """;

    // 415 Unsupported Media Type examples
    public static final String UNSUPPORTED_MEDIA_TYPE_EXAMPLE_EN = """
     {
       "timestamp": "2023-01-01T19:00:00",
       "path": "/api/resource",
       "status": 415,
       "error": "Unsupported Media Type",
       "message": "The server does not support the media type 'application/xml' specified in the 'Content-Type' header."
     }
 """;

    public static final String UNSUPPORTED_MEDIA_TYPE_EXAMPLE_PT = """
       {
         "timestamp": "2023-01-01T19:00:00",
         "path": "/api/resource",
         "status": 415,
         "error": "Unsupported Media Type",
         "message": "O servidor não suporta o tipo de mídia 'application/xml' especificado no cabeçalho 'Content-Type'."
       }
   """;

    // 413 Payload Too Large examples
    public static final String PAYLOAD_TOO_LARGE_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T19:30:00",
              "path": "/api/upload",
              "status": 413,
              "error": "Payload Too Large",
              "message": "The uploaded file size exceeds the allowed limit of 5MB."
            }
        """;

    public static final String PAYLOAD_TOO_LARGE_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T19:30:00",
              "path": "/api/upload",
              "status": 413,
              "error": "Payload Too Large",
              "message": "O tamanho do arquivo carregado excede o limite permitido de 5MB."
            }
        """;

    // 500 Internal Server Error examples
    public static final String INTERNAL_SERVER_ERROR_EXAMPLE_EN = """
            {
              "timestamp": "2023-01-01T20:00:00",
              "path": "/api/resource",
              "status": 500,
              "error": "Internal Server Error",
              "message": "An unexpected error occurred while processing the request."
            }
        """;

    public static final String INTERNAL_SERVER_ERROR_EXAMPLE_PT = """
            {
              "timestamp": "2023-01-01T20:00:00",
              "path": "/api/resource",
              "status": 500,
              "error": "Internal Server Error",
              "message": "Ocorreu um erro inesperado durante o processamento da solicitação."
            }
        """;
}