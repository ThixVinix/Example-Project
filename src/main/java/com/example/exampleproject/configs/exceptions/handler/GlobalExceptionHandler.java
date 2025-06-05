package com.example.exampleproject.configs.exceptions.handler;

import com.example.exampleproject.configs.exceptions.BaseError;
import com.example.exampleproject.configs.exceptions.ErrorMultipleResponse;
import com.example.exampleproject.configs.exceptions.ErrorSingleResponse;
import com.example.exampleproject.configs.exceptions.custom.BusinessException;
import com.example.exampleproject.configs.exceptions.custom.DataIntegrityViolationException;
import com.example.exampleproject.configs.exceptions.custom.ResourceNotFoundException;
import com.example.exampleproject.configs.exceptions.custom.UnauthorizedException;
import com.example.exampleproject.configs.exceptions.handler.helper.ExceptionHandlerMessageHelper;
import feign.FeignException;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponse(
            responseCode = "404",
            description = "<p><strong>English:</strong> Not Found. The requested resource could not be found on the " +
                    "server. This may happen if the resource does not exist, was removed, or the identifier provided " +
                    "is incorrect. Verify the request URL and parameters.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Não Encontrado. O recurso solicitado não pôde ser " +
                    "encontrado no servidor. Isso pode acontecer se o recurso não existe, foi removido ou o " +
                    "identificador fornecido está incorreto. Verifique a URL e os parâmetros da solicitação.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorSingleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 404 Not Found Example",
                                            summary = "English: Example of a 404 error response.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T12:00:00",
                                                          "path": "/api/resource/123",
                                                          "status": 404,
                                                          "error": "Not Found",
                                                          "message": "Resource with ID '123' was not found."
                                                        }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 404 Recurso Não Encontrado",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 404.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T12:00:00",
                                                          "path": "/api/resource/123",
                                                          "status": 404,
                                                          "error": "Not Found",
                                                          "message": "Recurso com ID '123' não foi encontrado."
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class, NoHandlerFoundException.class})
    protected ResponseEntity<ErrorSingleResponse> handleResourceNotFoundException(Exception ex,
                                                                                  WebRequest request) {
        log.error("Resource not found: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getNotFoundMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ApiResponse(
            responseCode = "405",
            description = "<p><strong>English:</strong> Method Not Allowed. The HTTP method used in the request is " +
                    "not supported by the resource. Ensure that you are using the correct method " +
                    "(e.g., GET, POST, PUT, DELETE) as documented for this API endpoint.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Método Não Permitido. O método HTTP usado na " +
                    "solicitação não é suportado pelo recurso. " +
                    "Certifique-se de que está usando o método correto (por exemplo, GET, POST, PUT, DELETE) conforme" +
                    " documentado para este endpoint da API.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorSingleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 405 Method Not Allowed",
                                            summary = "English: Example of a 405 error response when using an " +
                                                    "unsupported method.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T12:00:00",
                                                          "path": "/api/resource",
                                                          "status": 405,
                                                          "error": "Method Not Allowed",
                                                          "message": "POST is not supported for this resource."
                                                        }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 405 Método Não Permitido",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 405 ao " +
                                                    "usar um método não suportado.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T12:00:00",
                                                          "path": "/api/resource",
                                                          "status": 405,
                                                          "error": "Method Not Allowed",
                                                          "message": "POST não é suportado para este recurso."
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorSingleResponse> handleHttpRequestMethodNotSupportedException(Exception ex,
                                                                                                 WebRequest request) {
        log.error("HTTP request method not supported: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorSingleResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getMethodNotAllowedMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorSingleResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }


    @ApiResponse(
            responseCode = "400",
            description = "<p><strong>English:</strong> Bad Request. This error occurs when the server cannot " +
                    "process the request due to invalid syntax, missing required information, or incorrect data " +
                    "formatting. Verify the request parameters, body, and format before retrying.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Requisição Inválida. Este erro ocorre quando o " +
                    "servidor não pode processar a solicitação devido a sintaxe inválida, informações obrigatórias " +
                    "ausentes ou formatação incorreta de dados. Verifique os parâmetros da solicitação, corpo e " +
                    "formato antes de tentar novamente.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMultipleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 400 Bad Request",
                                            summary = "English: Example of a 400 error response due to invalid input.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T14:00:00",
                                                          "path": "/api/resource",
                                                          "status": 400,
                                                          "error": "Bad Request",
                                                          "messages": {
                                                            "field": "Field is required."
                                                          }
                                                        }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 400 Requisição Inválida",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 400 " +
                                                    "devido a entrada inválida.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T14:00:00",
                                                          "path": "/api/resource",
                                                          "status": 400,
                                                          "error": "Bad Request",
                                                          "messages": {
                                                            "field": "Campo é obrigatório."
                                                          }
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler({MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MissingRequestHeaderException.class,
            MissingPathVariableException.class,
            MissingServletRequestPartException.class,
            BusinessException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class,
            HandlerMethodValidationException.class,
            BindException.class})
    protected ResponseEntity<ErrorMultipleResponse> handleBadRequestException(Exception ex, WebRequest request) {
        log.error("Bad request: {}", ex.getMessage(), ex);

        ErrorMultipleResponse errorMultipleResponse = ErrorMultipleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .messages(ExceptionHandlerMessageHelper.getBadRequestMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorMultipleResponse, HttpStatus.BAD_REQUEST);
    }

    @ApiResponse(
            responseCode = "401",
            description = "<p><strong>English:</strong> Unauthorized. Authentication is required to access this " +
                    "resource, and the provided credentials are missing, invalid, or expired. Ensure that a valid " +
                    "'Authorization' header or token is included in the request.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Não Autorizado. A autenticação é necessária para " +
                    "acessar este recurso, e as credenciais fornecidas estão ausentes, inválidas ou expiradas. " +
                    "Certifique-se de que um cabeçalho 'Authorization' válido ou token esteja incluído na solicitação" +
                    ".</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorSingleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 401 Unauthorized",
                                            summary = "English: Example of a 401 error response caused by missing or " +
                                                    "invalid authentication credentials.",
                             value = """
                                         {
                                           "timestamp": "2023-01-01T12:00:00",
                                           "path": "/api/resource",
                                           "status": 401,
                                           "error": "Unauthorized",
                                           "message": "Authentication failed due to missing or invalid credentials."
                                         }
                                     """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 401 Não Autorizado",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 401 " +
                                                    "causada por credenciais de autenticação ausentes ou inválidas.",
                            value = """
                                        {
                                          "timestamp": "2023-01-01T12:00:00",
                                          "path": "/api/resource",
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "Falha na autenticação devido a credenciais ausentes ou inválidas."
                                        }
                                    """
                                   )
                            }
                    )
            }
    )
    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<ErrorSingleResponse> handleUnauthorizedException(Exception ex, WebRequest request) {
        log.error("Unauthorized: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorSingleResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getUnauthorizedMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorSingleResponse, HttpStatus.UNAUTHORIZED);
    }

    @ApiResponse(
            responseCode = "403",
            description = "<p><strong>English:</strong> Forbidden. The server understood the request but is refusing " +
                    "to authorize it. This error occurs when the client does not have the necessary permissions to " +
                    "access the resource. Ensure that the user has the required roles or permissions to perform the " +
                    "requested operation.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Proibido. O servidor entendeu a solicitação, mas está " +
                    "se recusando a autorizá-la. Este erro ocorre quando o cliente não tem as permissões necessárias " +
                    "para acessar o recurso. Certifique-se de que o usuário tenha as funções ou permissões " +
                    "necessárias para realizar a operação solicitada.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorSingleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 403 Forbidden",
                                            summary = "English: Example of a 403 error response when access is denied.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T15:00:00",
                                                          "path": "/api/protected-resource",
                                                          "status": 403,
                                                          "error": "Forbidden",
                                                          "message": "Access denied. You do not have sufficient
                                                          permissions to access this resource."
                                                        }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 403 Proibido",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 403 " +
                                                    "quando o acesso é negado.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T15:00:00",
                                                          "path": "/api/protected-resource",
                                                          "status": 403,
                                                          "error": "Forbidden",
                                                          "message": "Acesso negado. Você não tem permissões
                                                          suficientes para acessar este recurso."
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorSingleResponse> handleForbiddenException(Exception ex, WebRequest request) {
        log.error("Access denied: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorSingleResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getForbiddenMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorSingleResponse, HttpStatus.FORBIDDEN);
    }

    @ApiResponse(
            responseCode = "409",
            description = "<p><strong>English:</strong> Conflict. This occurs when the request cannot be completed " +
                    "due to a conflict in the current state of the resource. This could be caused by duplicate data, " +
                    "resource version conflicts, or business rules violations. Ensure that the data being sent is " +
                    "correct and does not conflict with the current resource state.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Conflito. Isso ocorre quando a solicitação não pode " +
                    "ser concluída devido a um conflito no estado atual do recurso. Isso pode ser causado por dados " +
                    "duplicados, conflitos de versão de recursos ou violações de regras de negócios. Certifique-se " +
                    "de que os dados enviados estão corretos e não entram em conflito com o estado atual do " +
                    "recurso.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorSingleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 409 Conflict",
                                            summary = "English: Example of a 409 error response caused by a " +
                                                    "duplicate entry.",
                                      value = """
                                                  {
                                                    "timestamp": "2023-01-01T16:00:00",
                                                    "path": "/api/resource",
                                                    "status": 409,
                                                    "error": "Conflict",
                                                    "message": "A resource with the identifier '123' already exists."
                                                  }
                                              """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 409 Conflito",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 409 " +
                                                    "causada por uma entrada duplicada.",
                                      value = """
                                                  {
                                                    "timestamp": "2023-01-01T16:00:00",
                                                    "path": "/api/resource",
                                                    "status": 409,
                                                    "error": "Conflict",
                                                    "message": "Um recurso com o identificador '123' já existe."
                                                  }
                                              """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorSingleResponse> handleConflictException(Exception ex,
                                                                            WebRequest request) {
        log.error("Conflict: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorSingleResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getConflictMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorSingleResponse, HttpStatus.CONFLICT);
    }

    @ApiResponse(
            responseCode = "408",
            description = "<p><strong>English:</strong> Request timed out. This occurs when the server couldn't " +
                    "complete the request within the timeout window. This could be due to server overload, a " +
                    "long-running operation, or connectivity issues.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Tempo limite da solicitação esgotado. Isso ocorre " +
                    "quando o servidor não consegue completar a solicitação dentro da janela de tempo limite. Isso " +
                    "pode ser devido à sobrecarga do servidor, uma operação de longa duração ou problemas de " +
                    "conectividade.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorSingleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 408 Request Timeout",
                                            summary = "English: Example of a 408 error response caused by a timeout.",
                                    value = """
                                                {
                                                  "timestamp": "2023-01-01T17:00:00",
                                                  "path": "/api/resource",
                                                  "status": 408,
                                                  "error": "Request Timeout",
                                                  "message": "The server timed out waiting for the request to complete."
                                                }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 408 Tempo Limite da Solicitação",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 408 " +
                                                    "causada por um tempo limite.",
                     value = """
                                 {
                                   "timestamp": "2023-01-01T17:00:00",
                                   "path": "/api/resource",
                                   "status": 408,
                                   "error": "Request Timeout",
                                   "message": "O servidor atingiu o tempo limite aguardando a conclusão da solicitação."
                                 }
                             """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler({TimeoutException.class, AsyncRequestTimeoutException.class})
    protected ResponseEntity<ErrorSingleResponse> handleTimeoutException(Exception ex, WebRequest request) {
        log.error("Request timed out: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorSingleResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.REQUEST_TIMEOUT.value())
                .error(HttpStatus.REQUEST_TIMEOUT.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getTimeoutMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorSingleResponse, HttpStatus.REQUEST_TIMEOUT);
    }

    @ApiResponse(
            responseCode = "406",
            description = "<p><strong>English:</strong> Not acceptable. This occurs when the 'Accept' header in the " +
                    "request specifies a response format that the server cannot provide. Ensure that the 'Accept' " +
                    "header is set to a format supported by the API, such as 'application/json'.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Não aceitável. Isso ocorre quando o cabeçalho " +
                    "'Accept' na solicitação especifica um formato de resposta que o servidor não pode fornecer. " +
                    "Certifique-se de que o cabeçalho 'Accept' esteja definido para um formato suportado pela API, " +
                    "como 'application/json'.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorSingleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 406 Not Acceptable",
                                            summary = "English: Example of a 406 error response caused by " +
                                                    "unsupported 'Accept' header value.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T18:00:00",
                                                          "path": "/api/resource",
                                                          "status": 406,
                                                          "error": "Not Acceptable",
                                                          "message": "The server cannot produce a response in the
                                                             requested format: 'application/xml'."
                                                        }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 406 Não Aceitável",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 406 " +
                                                    "causada por um valor " +
                                                    "de cabeçalho 'Accept' não suportado.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T18:00:00",
                                                          "path": "/api/resource",
                                                          "status": 406,
                                                          "error": "Not Acceptable",
                                                          "message": "O servidor não pode produzir uma resposta no
                                                             formato solicitado: 'application/xml'."
                                                        }
                                                    """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    protected ResponseEntity<ErrorSingleResponse> handleHttpMediaTypeNotAcceptableException(Exception ex,
                                                                                              WebRequest request) {
        log.error("Http Media Type Not Acceptable: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorSingleResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .error(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getHttpMediaTypeNotAcceptableException(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorSingleResponse, HttpStatus.NOT_ACCEPTABLE);
    }


    @ApiResponse(
            responseCode = "415",
            description = "<p><strong>English:</strong> Unsupported media type. This occurs when the media type " +
                    "provided in the request is not supported by the server. Ensure the 'Content-Type' header and " +
                    "request body are formatted correctly according to the API requirements.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Tipo de mídia não suportado. Isso ocorre quando o " +
                    "tipo de mídia fornecido na solicitação não é suportado pelo servidor. Certifique-se de que o " +
                    "cabeçalho 'Content-Type' e o corpo da solicitação estejam formatados corretamente de acordo com " +
                    "os requisitos da API.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMultipleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 415 Unsupported Media Type",
                                            summary = "English: Example of a 415 error response caused by an " +
                                                    "unsupported 'Content-Type' header.",
                                            value = """
                                                        {
                                                          "timestamp": "2023-01-01T19:00:00",
                                                          "path": "/api/resource",
                                                          "status": 415,
                                                          "error": "Unsupported Media Type",
                                                          "message": "The server does not support the media type
                                                             'application/xml' specified in the 'Content-Type' header."
                                                        }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 415 Tipo de Mídia Não Suportado",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 415 " +
                                                    "causada por um cabeçalho 'Content-Type' não suportado.",
                                           value = """
                                                       {
                                                         "timestamp": "2023-01-01T19:00:00",
                                                         "path": "/api/resource",
                                                         "status": 415,
                                                         "error": "Unsupported Media Type",
                                                         "message": "O servidor não suporta o tipo de mídia
                                                            'application/xml' especificado no cabeçalho 'Content-Type'."
                                                       }
                                                   """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<ErrorSingleResponse> handleHttpMediaTypeNotSupportedException(Exception ex,
                                                                                           WebRequest request) {
        log.error("Http Media Type Not Supported: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorSingleResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getHttpMediaTypeNotSupportedException(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorSingleResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ApiResponse(
            responseCode = "413",
            description = "<p><strong>English:</strong> Payload Too Large. This error occurs when the size of the " +
                    "uploaded file exceeds the limit supported by the server. Check the file size and the server's " +
                    "upload restrictions before retrying.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Carga Muito Grande. Este erro ocorre quando o tamanho " +
                    "do arquivo carregado excede o limite suportado pelo servidor. Verifique o tamanho do arquivo e " +
                    "as restrições de upload do servidor antes de tentar novamente.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorSingleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 413 Payload Too Large",
                                            summary = "English: Example of a 413 error response caused by an " +
                                                    "uploaded file that exceeds the allowed size.",
                                         value = """
                                                 {
                                                   "timestamp": "2023-01-01T19:30:00",
                                                   "path": "/api/upload",
                                                   "status": 413,
                                                   "error": "Payload Too Large",
                                                   "message": "The uploaded file size exceeds the allowed limit of 5MB."
                                                 }
                                                 """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 413 Carga Muito Grande",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 413 " +
                                                    "causada por um arquivo carregado que excede o tamanho permitido.",
                                 value = """
                                         {
                                           "timestamp": "2023-01-01T19:30:00",
                                           "path": "/api/upload",
                                           "status": 413,
                                           "error": "Payload Too Large",
                                           "message": "O tamanho do arquivo carregado excede o limite permitido de 5MB."
                                         }
                                         """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ErrorSingleResponse> handleMaxUploadSizeExceededException(
            Exception ex, WebRequest request) {
        log.error("Max Upload Size Exceeded: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .error(HttpStatus.PAYLOAD_TOO_LARGE.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getMaxUploadSizeExceededException(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ApiResponse(
            responseCode = "500",
            description = "<p><strong>English:</strong> Internal Server Error. This error occurs when the server " +
                    "encounters an unexpected condition that prevents it from fulfilling the request. Contact the " +
                    "API support team if the issue persists.</p>" +
                    "<p><strong>Brazilian Portuguese:</strong> Erro Interno do Servidor. Este erro ocorre quando o " +
                    "servidor encontra uma condição inesperada que o impede de atender à solicitação. Entre em " +
                    "contato com a equipe de suporte da API se o problema persistir.</p>",
            content = {
                    @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorSingleResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "English - 500 Internal Server Error",
                                            summary = "English: Example of a 500 error response caused by an " +
                                                    "unexpected server-side failure.",
                               value = """
                                           {
                                             "timestamp": "2023-01-01T20:00:00",
                                             "path": "/api/resource",
                                             "status": 500,
                                             "error": "Internal Server Error",
                                             "message": "An unexpected error occurred while processing the request."
                                           }
                                       """
                                    ),
                                    @ExampleObject(
                                            name = "Brazilian Portuguese - 500 Erro Interno do Servidor",
                                            summary = "Brazilian Portuguese: Exemplo de uma resposta de erro 500 " +
                                                    "causada por uma falha inesperada do lado do servidor.",
                           value = """
                                       {
                                         "timestamp": "2023-01-01T20:00:00",
                                         "path": "/api/resource",
                                         "status": 500,
                                         "error": "Internal Server Error",
                                         "message": "Ocorreu um erro inesperado durante o processamento da solicitação."
                                       }
                                   """
                                    )
                            }
                    )
            }
    )
    @ExceptionHandler({RuntimeException.class, Exception.class, Throwable.class})
    protected ResponseEntity<ErrorSingleResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorSingleResponse errorSingleResponse = ErrorSingleResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ExceptionHandlerMessageHelper.getInternalServerErrorMessage(ex))
                .path(request.getDescription(Boolean.FALSE))
                .build();

        return new ResponseEntity<>(errorSingleResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @SuppressWarnings("squid:S1452")
    @ExceptionHandler(FeignException.class)
    protected ResponseEntity<? extends BaseError> handleFeignClientException(FeignException e, WebRequest request) {
        String requestUri = request.getDescription(false);
        HttpStatus status = HttpStatus.resolve(e.status());
        int statusFeign = e.status();

        logFeignErrorDetails(e, requestUri, status);

        if (statusFeign == -1 || Objects.isNull(status)) {
            return this.handleGlobalException(e, request);
        }

        return getResponseByStatus(status, e, request);
    }

    private void logFeignErrorDetails(FeignException e, String requestUri, HttpStatus status) {
        String responseBody = e.responseBody().map(Object::toString).orElse("No response body");
        String requestHeaders = e.request().headers().toString();
        String responseHeaders = e.responseHeaders().toString();

        log.error("""
                        FEIGN CLIENT ERROR:
                        URI: {}
                        Status: {}
                        Request Headers: {}
                        Response Headers: {}
                        Response Body: {}
                        """,
                requestUri,
                status != null ? status.name() : "Unknown Status",
                requestHeaders,
                responseHeaders,
                responseBody);
    }

    private ResponseEntity<? extends BaseError> getResponseByStatus(HttpStatus status,
                                                                    FeignException e,
                                                                    WebRequest request) {
        return switch (status) {
            case BAD_REQUEST -> this.handleBadRequestException(e, request);
            case UNAUTHORIZED -> this.handleUnauthorizedException(e, request);
            case FORBIDDEN -> this.handleForbiddenException(e, request);
            case NOT_FOUND -> this.handleResourceNotFoundException(e, request);
            case METHOD_NOT_ALLOWED -> this.handleHttpRequestMethodNotSupportedException(e, request);
            case NOT_ACCEPTABLE -> this.handleHttpMediaTypeNotAcceptableException(e, request);
            case REQUEST_TIMEOUT -> this.handleTimeoutException(e, request);
            case CONFLICT -> this.handleConflictException(e, request);
            case UNSUPPORTED_MEDIA_TYPE -> this.handleHttpMediaTypeNotSupportedException(e, request);
            case PAYLOAD_TOO_LARGE -> this.handleMaxUploadSizeExceededException(e, request);
            default -> this.handleGlobalException(e, request);
        };
    }
}
