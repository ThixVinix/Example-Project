package com.example.exampleproject.configs.swagger;

/**
 * Utility class for providing standard HTTP status descriptions in both English and
 * Brazilian Portuguese for usage in API documentation or error responses.
 *
 * <p>This class is designed as a centralized resource for consistent messaging
 * regarding HTTP status codes and their meanings. Descriptions provided are in HTML
 * format to support rich text rendering.</p>
 *
 * <p><b>Note:</b> This class cannot be instantiated as it is a utility class.</p>
 */
public final class SwaggerHttpDescriptionsHelper {

    private SwaggerHttpDescriptionsHelper() {
       throw new IllegalStateException("Utility class cannot be instantiated");
    }

    // 404 Not Found description
    public static final String NOT_FOUND_DESCRIPTION = """
            <p><strong>English:</strong> Not Found. The requested resource could not be found on the \
            server. This may happen if the resource does not exist, was removed, or the identifier provided \
            is incorrect. Verify the request URL and parameters.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Não Encontrado. O recurso solicitado não pôde ser \
            encontrado no servidor. Isso pode acontecer se o recurso não existe, foi removido ou o \
            identificador fornecido está incorreto. Verifique a URL e os parâmetros da solicitação.</p>
            """;

    // 405 Method Not Allowed description
    public static final String METHOD_NOT_ALLOWED_DESCRIPTION = """
            <p><strong>English:</strong> Method Not Allowed. The HTTP method used in the request is \
            not supported by the resource. Ensure that you are using the correct method \
            (e.g., GET, POST, PUT, DELETE) as documented for this API endpoint.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Método Não Permitido. O método HTTP usado na \
            solicitação não é suportado pelo recurso. \
            Certifique-se de que está usando o método correto (por exemplo, GET, POST, PUT, DELETE) conforme \
            documentado para este endpoint da API.</p>
            """;

    // 400 Bad Request description
    public static final String BAD_REQUEST_DESCRIPTION = """
            <p><strong>English:</strong> Bad Request. This error occurs when the server cannot \
            process the request due to invalid syntax, missing required information, or incorrect data \
            formatting. Verify the request parameters, body, and format before retrying.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Requisição Inválida. Este erro ocorre quando o \
            servidor não pode processar a solicitação devido a sintaxe inválida, informações obrigatórias \
            ausentes ou formatação incorreta de dados. Verifique os parâmetros da solicitação, corpo e \
            formato antes de tentar novamente.</p>
            """;

    // 401 Unauthorized description
    public static final String UNAUTHORIZED_DESCRIPTION = """
            <p><strong>English:</strong> Unauthorized. Authentication is required to access this \
            resource, and the provided credentials are missing, invalid, or expired. Ensure that a valid \
            'Authorization' header or token is included in the request.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Não Autorizado. A autenticação é necessária para \
            acessar este recurso, e as credenciais fornecidas estão ausentes, inválidas ou expiradas. \
            Certifique-se de que um cabeçalho 'Authorization' válido ou token esteja incluído na solicitação\
            .</p>""";

    // 403 Forbidden description
    public static final String FORBIDDEN_DESCRIPTION = """
            <p><strong>English:</strong> Forbidden. The server understood the request but is refusing \
            to authorize it. This error occurs when the client does not have the necessary permissions to \
            access the resource. Ensure that the user has the required roles or permissions to perform the \
            requested operation.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Proibido. O servidor entendeu a solicitação, mas está \
            se recusando a autorizá-la. Este erro ocorre quando o cliente não tem as permissões necessárias \
            para acessar o recurso. Certifique-se de que o usuário tenha as funções ou permissões \
            necessárias para realizar a operação solicitada.</p>
            """;

    // 409 Conflict description
    public static final String CONFLICT_DESCRIPTION = """
            <p><strong>English:</strong> Conflict. This occurs when the request cannot be completed \
            due to a conflict in the current state of the resource. This could be caused by duplicate data, \
            resource version conflicts, or business rules violations. Ensure that the data being sent is \
            correct and does not conflict with the current resource state.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Conflito. Isso ocorre quando a solicitação não pode \
            ser concluída devido a um conflito no estado atual do recurso. Isso pode ser causado por dados \
            duplicados, conflitos de versão de recursos ou violações de regras de negócios. Certifique-se \
            de que os dados enviados estão corretos e não entram em conflito com o estado atual do \
            recurso.</p>
            """;

    // 408 Request Timeout description
    public static final String REQUEST_TIMEOUT_DESCRIPTION = """
            <p><strong>English:</strong> Request timed out. This occurs when the server couldn't \
            complete the request within the timeout window. This could be due to server overload, a \
            long-running operation, or connectivity issues.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Tempo limite da solicitação esgotado. Isso ocorre \
            quando o servidor não consegue completar a solicitação dentro da janela de tempo limite. Isso \
            pode ser devido à sobrecarga do servidor, uma operação de longa duração ou problemas de \
            conectividade.</p>
            """;

    // 503 Service Unavailable description
    public static final String SERVICE_UNAVAILABLE_DESCRIPTION = """
            <p><strong>English:</strong> Service Unavailable. This occurs when the server is \
            temporarily unable to handle the request due to maintenance or overload. This is often \
            caused by asynchronous request timeouts or server-side resource constraints.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Serviço Indisponível. Isso ocorre quando o servidor \
            está temporariamente incapaz de processar a solicitação devido à manutenção ou sobrecarga. \
            Isso geralmente é causado por tempos limite de solicitação assíncrona ou restrições de recursos \
            do lado do servidor.</p>
            """;

    // 406 Not Acceptable description
    public static final String NOT_ACCEPTABLE_DESCRIPTION = """
            <p><strong>English:</strong> Not acceptable. This occurs when the 'Accept' header in the \
            request specifies a response format that the server cannot provide. Ensure that the 'Accept' \
            header is set to a format supported by the API, such as 'application/json'.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Não aceitável. Isso ocorre quando o cabeçalho \
            'Accept' na solicitação especifica um formato de resposta que o servidor não pode fornecer. \
            Certifique-se de que o cabeçalho 'Accept' esteja definido para um formato suportado pela API, \
            como 'application/json'.</p>
            """;

    // 415 Unsupported Media Type description
    public static final String UNSUPPORTED_MEDIA_TYPE_DESCRIPTION = """
            <p><strong>English:</strong> Unsupported media type. This occurs when the media type \
            provided in the request is not supported by the server. Ensure the 'Content-Type' header and \
            request body are formatted correctly according to the API requirements.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Tipo de mídia não suportado. Isso ocorre quando o \
            tipo de mídia fornecido na solicitação não é suportado pelo servidor. Certifique-se de que o \
            cabeçalho 'Content-Type' e o corpo da solicitação estejam formatados corretamente de acordo com \
            os requisitos da API.</p>
            """;

    // 413 Payload Too Large description
    public static final String PAYLOAD_TOO_LARGE_DESCRIPTION = """
            <p><strong>English:</strong> Payload Too Large. This error occurs when the size of the \
            uploaded file exceeds the limit supported by the server. Check the file size and the server's \
            upload restrictions before retrying.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Carga Muito Grande. Este erro ocorre quando o tamanho \
            do arquivo carregado excede o limite suportado pelo servidor. Verifique o tamanho do arquivo e \
            as restrições de upload do servidor antes de tentar novamente.</p>
            """;

    // 500 Internal Server Error description
    public static final String INTERNAL_SERVER_ERROR_DESCRIPTION = """
            <p><strong>English:</strong> Internal Server Error. This error occurs when the server \
            encounters an unexpected condition that prevents it from fulfilling the request. Contact the \
            API support team if the issue persists.</p>\
            
            <p><strong>Brazilian Portuguese:</strong> Erro Interno do Servidor. Este erro ocorre quando o \
            servidor encontra uma condição inesperada que o impede de atender à solicitação. Entre em \
            contato com a equipe de suporte da API se o problema persistir.</p>
            """;
}
