# Example Project

![Java Version](https://img.shields.io/badge/Java-22-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen)
![License](https://img.shields.io/badge/License-MIT-lightgrey)


Este é um projeto de exemplo que demonstra o desenvolvimento de uma **API RESTful** usando Spring Boot. 
O projeto inclui exemplos de:

- ✅ Validação de entrada
- 🔄 Integração com APIs externas
- 📝 Documentação de API com Swagger/OpenAPI
- 📅 Manipulação de datas e horas
- 🔤 Tratamento de enumerações

## 📚 Sumário
1. [🛠️ Tecnologias](#-tecnologias)
2. [📋 Pré-requisitos](#-pré-requisitos)
3. [🚀 Como Executar o Projeto](#-como-executar-o-projeto)
4. [🧪 Como Executar os Testes Unitários](#-como-executar-os-testes-unitários)
5. [🏗️ Arquitetura do Projeto](#-arquitetura-do-projeto)
    - [Definição de Pacotes](#definição-de-pacotes)
    - [Estrutura de Diretórios](#estrutura-de-diretórios)
    - [Convenções de API REST](#convenções-de-api-rest)
6. [💻 Implementações Técnicas](#-implementações-técnicas)
    - [Padrão de Integração com WebClient](#-padrão-de-integração-com-webclient)
    - [Anotações Customizadas para Validação](#-anotações-customizadas-para-validação)
    - [MessageUtils para Internacionalização](#-messageutils-para-internacionalização)
7. [📐 Princípios SOLID](#-princípios-solid)
8. [📚 Recursos Adicionais](#-recursos-adicionais)


## 🛠️ Tecnologias

### Principais Tecnologias

- [Java 22](https://docs.oracle.com/en/java/javase/22/) - Linguagem de programação
- [Spring Boot 3.3.2](https://spring.io/) - Framework para desenvolvimento de aplicações
- [Spring WebFlux WebClient](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html) - Cliente HTTP reativo para chamadas a serviços RESTful
- [Swagger/OpenAPI](https://swagger.io/docs/) - Documentação de API
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/) - Framework de testes
- [Lombok](https://projectlombok.org/features/) - Redução de código boilerplate
- [Jackson](https://github.com/FasterXML/jackson-docs) - Serialização/deserialização de JSON
- [Apache Commons Lang3](https://commons.apache.org/proper/commons-lang/) - Utilitários para manipulação de strings, números, objetos, etc.

[🔼 _**Retornar ao sumário**_](#-sumário)

## 📋 Pré-requisitos

- JDK 22
- Gradle 8.x

[🔼 _**Retornar ao sumário**_](#-sumário)

## 🚀 Como Executar o Projeto

1. **Clone o repositório**:
   ```bash
   git clone https://github.com/seu-usuario/Example-Project.git
   cd Example-Project
   ```

2. **Compile o projeto**:
   ```bash
   ./gradlew build
   ```

3. **Execute a aplicação**:
   ```bash
   ./gradlew bootRun
   ```

4. **Acesse a aplicação**:
   - API: http://localhost:8080
   - Documentação Swagger: http://localhost:8080/swagger-ui.html

[🔼 _**Retornar ao sumário**_](#-sumário)

## 🧪 Como Executar os Testes Unitários

Para executar **todos os testes unitários**:
```bash
./gradlew test
```

Para executar um **teste específico**:
```bash
./gradlew test --tests "com.example.exampleproject.NomeDoTeste"
```

[🔼 _**Retornar ao sumário**_](#-sumário)

## 🏗️ Arquitetura do Projeto

### Definição de Pacotes

A estrutura de pacotes (ou namespaces) é definida seguindo a seguinte regra:

```
<company-domain>.<bounded-context>.<layer>
```

Segue exemplo do pacote onde ficam os controllers:

```
com.example.exampleproject.controllers
```

### Estrutura de Diretórios

```
src
├── main
│   ├── java
│   │   └── com
│   │       └── example
│   │           └── exampleproject
│   │               ├── clients      # Clientes para APIs externas
│   │               ├── configs      # Configurações da aplicação
│   │               ├── controllers  # Controladores REST
│   │               ├── dto          # Objetos de transferência de dados
│   │               ├── enums        # Enumerações
│   │               ├── services     # Serviços de negócio
│   │               └── utils        # Classes utilitárias
│   └── resources                    # Recursos da aplicação
└── test
    └── java                         # Testes unitários
        └── com
            └── example
                └── exampleproject
```

### Convenções de API REST

#### 📝 Princípios Básicos

* **Use substantivos no URI:**
  API's REST devem ser desenhadas para Recursos, que podem ser entidades ou serviços, portanto devem ser sempre
  substantivos. Por exemplo, em vez de `/create-user`, use `/users`

* **Plurais ou Singulares:** 
  Geralmente, preferimos usar plurais para representar coleções de recursos.
  Por exemplo:
  ```
  GET /users/123    # Recupera um usuário específico
  POST /users       # Adiciona um novo usuário à coleção
  ```

* **Hierarquia de recursos:** 
  Se um recurso contiver sub-recursos, represente essa hierarquia na URI.
  Por exemplo:
  ```
  GET /users/123/posts/1    # Recupera o post 1 do usuário 123
  ```

* **Hífen para múltiplas palavras:** 
  Use hífen para separar palavras em URIs complexas.
  ```
  POST /affiliateds/12512/sales/smart-installment-payment/simulate
  ```

#### 🔄 Verbos HTTP

Os verbos HTTP definem a ação a ser executada sobre os recursos:

| Recurso    | GET (Leitura)                     | POST (Criação)        | PUT (Atualização)                  | DELETE (Remoção)                 |
|------------|-----------------------------------|-----------------------|------------------------------------|----------------------------------|
| /users     | Retorna a lista de usuários.      | Cria um novo usuário. | Atualização em lote de usuários.   | Remove todos os usuários.        |
| /users/123 | Retorna um usuário em específico. | Method not allowed.   | Atualiza um usuário em específico. | Remove um usuário em específico. |

#### 🔍 Filtragem e Busca

Mantenha URIs simples e use parâmetros de consulta para filtragem:

* **Filtragem básica:**
  ```
  GET /users/123/posts?state=published
  ```

* **Busca avançada:**
  ```
  GET /users/123/posts?state=published&tag=scala
  ```

* **Ordenação:**
  ```
  GET /users/123/posts?sort=-updated-at    # Ordenação descendente por data de atualização
  ```

#### 📤 Respostas

Os métodos POST ou PUT devem retornar uma representação do recurso atualizado com o código de _status_ apropriado.

[🔼 _**Retornar ao sumário**_](#-sumário)

## 💻 Implementações Técnicas

### 🔄 Padrão de Integração com WebClient

O projeto utiliza o `Spring WebFlux WebClient` para integrações HTTP com serviços externos. O padrão implementado segue uma abordagem em camadas e prioriza um cliente leve, com controle fino de timeouts, headers e tratamento de erros:

1. **Configuração**:
   Um `WebClient` (ou `WebClient.Builder`) é exposto via configuração para reutilização e padronização de headers, baseUrl, codecs e timeouts.
   ```java
   @Configuration
   public class WebClientConfig {
       @Bean
       public WebClient webClient(WebClient.Builder builder) {
           return builder
               .baseUrl("${external.apis.base-url:}") // opcional
               .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
               .build();
       }
   }
   ```

2. **Camada de Serviço**:
   A lógica de negócio utiliza o `WebClient` para realizar chamadas externas. É possível trabalhar de forma reativa (Mono/Flux) ou bloquear quando necessário.
   ```java
   @Service
   public class ExampleServiceImpl implements ExampleService {
       private final WebClient webClient;
       
       @Autowired
       public ExampleServiceImpl(WebClient webClient) {
           this.webClient = webClient;
       }

       @Override
       public ExampleObject getById(Long id) {
           return webClient
               .get()
               .uri(uriBuilder -> uriBuilder.path("/context/{id}").build(id))
               .retrieve()
               .onStatus(HttpStatusCode::isError, clientResponse ->
                   clientResponse.bodyToMono(String.class)
                       .map(body -> new RuntimeException("Erro na chamada externa: " + body))
               )
               .bodyToMono(ExampleObject.class)
               .block(); // ou retorne Mono<ExampleObject> se preferir reativo
       }

       // outros métodos...
   }
   ```

3. **Tratamento de Erros e Observabilidade**:
   Padronize o mapeamento de erros, logging e métricas (Micrometer) na camada de serviço ou via filtros (`ExchangeFilterFunction`).
   ```java
   @Bean
   public WebClient webClientWithFilters(WebClient.Builder builder) {
       return builder
           .filter((request, next) -> {
               long start = System.currentTimeMillis();
               return next.exchange(request)
                   .doOnNext(resp -> {
                       long ms = System.currentTimeMillis() - start;
                       // log/metricas por request
                   });
           })
           .build();
   }
   ```

#### Benefícios do Padrão
- ✅ **Leve e flexível**: Sem proxies gerados; controle total sobre requisições HTTP
- ✅ **Reativo ou bloqueante**: Suporta `Mono/Flux` e também `.block()` quando apropriado
- ✅ **Tratamento de erros granular**: `onStatus`, filtros e mapeamentos customizados
- ✅ **Testabilidade**: Facilita testes com `MockWebServer`/`WireMock` e mocks de `WebClient`
- ✅ **Observabilidade**: Integração simples com logs, métricas e tracing (Micrometer/Brave/OpenTelemetry)

### ✅ Anotações Customizadas para Validação

O projeto implementa diversas anotações customizadas para validação de dados, seguindo o padrão do Bean Validation:

#### `@Base64FileValidation`
Valida se uma string (ou coleção de strings) contém arquivos Base64 válidos, verificando formato, tamanho, tipo MIME e quantidade.

**Características:**
- ✅ Suporta validação de strings individuais, listas e mapas
- ✅ Validação de tipos MIME permitidos
- ✅ Controle de tamanho individual e total dos arquivos
- ✅ Limite de quantidade de arquivos em coleções

```java
// Validação de arquivo único
@Base64FileValidation(maxSizePerFileInMB = 5, allowedTypes = {"image/jpeg", "image/png"})
private String profileImage;

// Validação de lista de arquivos
@Base64FileValidation(maxSizePerFileInMB = 4, maxTotalSizeInMB = 12, maxFileCount = 3, allowedTypes = {"image/jpeg", "image/png"})
private List<String> attachments;

// Validação de mapa de arquivos
@Base64FileValidation(maxSizePerFileInMB = 4, maxTotalSizeInMB = 12, maxFileCount = 3, allowedTypes = {"image/jpeg", "image/png"})
private Map<String, String> documentFiles;
```

#### `@MultipartFileValidation`
Valida arquivos MultipartFile, verificando tipo MIME, tamanho individual, quantidade e tamanho total.

**Características:**
- ✅ Suporta validação de arquivos individuais e listas
- ✅ Validação de tipos MIME com conjunto padrão abrangente
- ✅ Controle de tamanho individual e total dos arquivos
- ✅ Limite de quantidade de arquivos

```java
// Validação de arquivo único
@MultipartFileValidation(maxSizeInMB = 10,  allowedTypes = {"application/pdf"})
private MultipartFile document;

// Validação de lista de arquivos
@MultipartFileValidation(maxSizeInMB = 4, maxTotalSizeMB = 12, maxFileCount = 3,  allowedTypes = {"image/png"})
private List<MultipartFile> images;
```

#### `@EnumCodeValidation`
Valida se um valor numérico corresponde ao código de uma constante em uma classe Enum específica.

**Características:**
- ✅ Validação baseada em códigos numéricos de enums
- ✅ Suporte a enums que implementam interfaces com método `getCode()`
- ✅ Mensagens de erro localizadas

```java
@EnumCodeValidation(enumClass = StatusEnum.class)
private Integer statusCode;
```

#### `@EnumValueValidation`
Valida se um valor de string corresponde ao valor (name) de uma constante em uma classe Enum específica.

**Características:**
- ✅ Validação baseada nos nomes das constantes do enum
- ✅ Comparação case-sensitive
- ✅ Mensagens de erro localizadas

```java
@EnumValueValidation(enumClass = StatusEnum.class)
private String statusValue;
```

#### `@DateRangeValidation`
Valida se um par de datas forma um intervalo válido, onde a data inicial deve ser anterior ou igual à data final.

**Características:**
- ✅ Validação de intervalos de datas em nível de classe
- ✅ Suporte a diferentes tipos de data (LocalDate, LocalDateTime, etc.)
- ✅ Configuração flexível dos nomes dos campos
- ✅ Permite datas iguais por padrão

```java
@DateRangeValidation(startDateField = "startDate", endDateField = "endDate")
public class DateRangeRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}

// Múltiplas validações de intervalo na mesma classe
@ValidDateRanges({
    @DateRangeValidation(startDateField = "checkIn", endDateField = "checkOut"),
    @DateRangeValidation(startDateField = "validFrom", endDateField = "validUntil")
})
public class ReservationRequest {
    private LocalDate checkIn;
    private LocalDate checkOut;
    private LocalDate validFrom;
    private LocalDate validUntil;
}
```

#### `@CpfCnpjValidation`
Valida se uma string contém um CPF (Cadastro de Pessoas Físicas) ou CNPJ (Cadastro Nacional da Pessoa Jurídica) brasileiro válido.

**Características:**
- ✅ Validação de CPF (11 dígitos) e CNPJ (14 dígitos)
- ✅ Verificação de dígitos verificadores
- ✅ Aceita formatos com ou sem máscara
- ✅ Mensagens de erro localizadas

```java
@CpfCnpjValidation
private String document; // Aceita: "12345678901", "123.456.789-01", "12345678000195", "12.345.678/0001-95"
```

#### Implementação
Cada anotação customizada possui um validador correspondente que implementa a interface `ConstraintValidator`:

```java
public class EnumCodeValidator implements ConstraintValidator<EnumCodeValidation, Integer> {
    // Implementação da validação
}
```

### 🌐 MessageUtils para Internacionalização

O projeto utiliza o `MessageUtils` para obter mensagens localizadas através do `MessageSource` do Spring:

#### Implementação
`MessageUtils` é uma classe utilitária que encapsula o acesso ao `MessageSource` do Spring:

```java
@Component
public class MessageUtils {
    private static MessageSource messageSourceStatic;

    @PostConstruct
    private synchronized void init() {
        messageSourceStatic = messageSource;
    }

    public static String getMessage(String key) {
        return messageSourceStatic.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String key, Object... args) {
        return messageSourceStatic.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    // Outros métodos para idiomas específicos...
}
```

#### Arquivos de Mensagens
As mensagens são definidas em arquivos properties específicos para cada idioma:
- `messages_pt_BR.properties` (Português do Brasil)
- `messages_en.properties` (Inglês)

#### Exemplos de Uso

<details>
  <summary>📋 Clique para ver exemplos de uso</summary>

1. **Em validadores customizados**:
   ```java
   String errorMessage = MessageUtils.getMessage(
       "msg.validation.request.field.enum.invalid.code",
       invalidValue,
       validValues
   );
   ```

2. **Em tratamento de exceções**:
   ```java
   String errorMessage = MessageUtils.getMessage("msg.exception.handler.resource.not.found");
   ```

3. **Em deserializadores customizados**:
   ```java
   String errorMessage = MessageUtils.getMessage(
       "msg.deserialization.invalid.datetime.format",
       fieldName, 
       value, 
       pattern
   );
   ```
</details>

#### Benefícios
- 🌍 **Internacionalização**: Suporte a múltiplos idiomas
- 📦 **Centralização**: Mensagens definidas em um único lugar
- 🔄 **Parametrização**: Suporte a parâmetros nas mensagens
- 🔄 **Consistência**: Padronização das mensagens em toda a aplicação

[🔼 _**Retornar ao sumário**_](#-sumário)

## 📐 Princípios SOLID

Este projeto foi desenvolvido seguindo os princípios SOLID, fundamentais para criar código limpo, manutenível e extensível:

### 🔍 Single Responsibility Principle (SRP)
> "Uma classe deve ter apenas um motivo para mudar."

- ✅ Cada classe deve ter uma única responsabilidade
- 📝 **Exemplo**: Separar lógica de negócios (`Services`) da lógica de apresentação (`Controllers`)

### 🚪 Open/Closed Principle (OCP)
> "Entidades de software devem estar abertas para extensão, mas fechadas para modificação."

- ✅ Classes devem estar abertas para extensão, mas fechadas para modificação
- 📝 **Exemplo**: Usar interfaces e injeção de dependência para permitir extensões sem modificar o código existente

### 🔄 Liskov Substitution Principle (LSP)
> "Subtipos devem ser substituíveis por seus tipos-base."

- ✅ Implementações de interfaces devem respeitar os contratos definidos
- 📝 **Exemplo**: Uma classe `ExampleServiceImpl` deve poder substituir completamente a interface `ExampleService`

### 🧩 Interface Segregation Principle (ISP)
> "Clientes não devem ser forçados a depender de interfaces que não utilizam."

- ✅ Criar interfaces específicas em vez de interfaces genéricas
- 📝 **Exemplo**: Dividir interfaces grandes em interfaces menores e mais específicas

### 🔌 Dependency Inversion Principle (DIP)
> "Módulos de alto nível não devem depender de módulos de baixo nível. Ambos devem depender de abstrações."

- ✅ Usar injeção de dependência e programar para interfaces, não implementações
- 📝 **Exemplo**: Injetar `ExampleService` em vez de `ExampleServiceImpl`

[🔼 _**Retornar ao sumário**_](#-sumário)

## 📚 Recursos Adicionais

- [Guia de Boas Práticas para Design de API REST](https://medium.com/hashmapinc/rest-good-practices-for-api-design-881439796dc9)
- [Princípios SOLID em Java](https://www.baeldung.com/solid-principles)

[🔼 _**Retornar ao sumário**_](#-sumário)
