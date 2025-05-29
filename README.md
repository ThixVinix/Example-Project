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
    - [Padrão de Integração com FeignClient](#-padrão-de-integração-com-feignclient)
    - [Anotações Customizadas para Validação](#-anotações-customizadas-para-validação)
    - [MessageUtils para Internacionalização](#-messageutils-para-internacionalização)
7. [📐 Princípios SOLID](#-princípios-solid)
8. [📚 Recursos Adicionais](#-recursos-adicionais)


## 🛠️ Tecnologias

### Principais Tecnologias

- [Java 22](https://docs.oracle.com/en/java/javase/22/) - Linguagem de programação
- [Spring Boot 3.3.2](https://spring.io/) - Framework para desenvolvimento de aplicações
- [Spring Cloud OpenFeign](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/) - Cliente HTTP para chamadas a serviços RESTful
- [Swagger/OpenAPI](https://swagger.io/docs/) - Documentação de API
- [JUnit 5](https://junit.org/junit5/docs/current/user-guide/) - Framework de testes
- [Lombok](https://projectlombok.org/features/) - Redução de código boilerplate
- [Jackson](https://github.com/FasterXML/jackson-docs) - Serialização/deserialização de JSON
- [Apache Commons Lang3](https://commons.apache.org/proper/commons-lang/) - Utilitários para manipulação de strings, números, objetos, etc.

## 📋 Pré-requisitos

- JDK 22
- Gradle 8.x

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

## 🧪 Como Executar os Testes Unitários

Para executar **todos os testes unitários**:
```bash
./gradlew test
```

Para executar um **teste específico**:
```bash
./gradlew test --tests "com.example.exampleproject.NomeDoTeste"
```

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

## 💻 Implementações Técnicas

### 🔄 Padrão de Integração com FeignClient

O projeto utiliza o Spring Cloud OpenFeign para simplificar a integração com APIs externas. O padrão implementado segue uma abordagem em camadas:

1. **Interfaces de Cliente**: 
   Definidas com a anotação `@FeignClient`, especificando o nome do serviço e a URL base.
   ```java
   @FeignClient(name = "jsonplaceholder", url = "https://jsonplaceholder.typicode.com")
   public interface JsonPlaceholderClient {
       @GetMapping("/posts/{id}")
       JsonPlaceholderPost getPostById(@PathVariable("id") Long id);

       // outros métodos...
   }
   ```

2. **Camada de Serviço**: 
   Implementa a lógica de negócios e utiliza os clientes Feign para fazer as chamadas externas.
   ```java
   @Service
   public class JsonPlaceholderServiceImpl implements JsonPlaceholderService {
       private final JsonPlaceholderClient jsonPlaceholderClient;

       @Autowired
       public JsonPlaceholderServiceImpl(JsonPlaceholderClient jsonPlaceholderClient) {
           this.jsonPlaceholderClient = jsonPlaceholderClient;
       }

       @Override
       public JsonPlaceholderPost getPostById(Long id) {
           return jsonPlaceholderClient.getPostById(id);
       }

       // outros métodos...
   }
   ```

3. **Configuração**: 
   A anotação `@EnableFeignClients` na classe principal habilita o suporte ao Feign.
   ```java
   @SpringBootApplication
   @EnableFeignClients
   public class ExampleProjectApplication {
       // ...
   }
   ```

#### Benefícios do Padrão
- ✅ **Desacoplamento**: Separa a lógica de integração da lógica de negócios
- ✅ **Testabilidade**: Facilita a criação de mocks para testes unitários
- ✅ **Manutenibilidade**: Centraliza a configuração de chamadas externas
- ✅ **Declarativo**: Utiliza anotações para definir endpoints e parâmetros

### ✅ Anotações Customizadas para Validação

O projeto implementa diversas anotações customizadas para validação de dados, seguindo o padrão do Bean Validation:

#### `@EnumCodeValidation`
Valida se um valor numérico corresponde ao código de uma constante em uma classe Enum específica.

```java
@EnumCodeValidation(enumClass = StatusEnum.class)
private Integer statusCode;
```

#### `@EnumValueValidation`
Valida se um valor de string corresponde ao valor de uma constante em uma classe Enum específica.

```java
@EnumValueValidation(enumClass = StatusEnum.class)
private String statusValue;
```

#### `@DateRangeValidation`
Valida se um par de datas forma um intervalo válido, onde a primeira data deve ser anterior à segunda.

```java
@DateRangeValidation(startDateField = "startDate", endDateField = "endDate")
public class DateRangeRequest {
    private LocalDate startDate;
    private LocalDate endDate;
}
```

#### `@Base64FileValidation`
Valida se uma string é um arquivo Base64 válido, verificando formato, tamanho e tipo.

```java
@Base64FileValidation(maxSizeMB = 5, allowedTypes = {"image/jpeg", "image/png"})
private String fileBase64;
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
- 📝 **Exemplo**: Uma classe `JsonPlaceholderServiceImpl` deve poder substituir completamente a interface `JsonPlaceholderService`

### 🧩 Interface Segregation Principle (ISP)
> "Clientes não devem ser forçados a depender de interfaces que não utilizam."

- ✅ Criar interfaces específicas em vez de interfaces genéricas
- 📝 **Exemplo**: Dividir interfaces grandes em interfaces menores e mais específicas

### 🔌 Dependency Inversion Principle (DIP)
> "Módulos de alto nível não devem depender de módulos de baixo nível. Ambos devem depender de abstrações."

- ✅ Usar injeção de dependência e programar para interfaces, não implementações
- 📝 **Exemplo**: Injetar `JsonPlaceholderService` em vez de `JsonPlaceholderServiceImpl`

## 📚 Recursos Adicionais

- [Guia de Boas Práticas para Design de API REST](https://medium.com/hashmapinc/rest-good-practices-for-api-design-881439796dc9)
- [Princípios SOLID em Java](https://www.baeldung.com/solid-principles)