# 💳 Credit Card Management System

Sistema completo de gerenciamento de cartões de crédito desenvolvido com **Spring Boot**, oferecendo tanto interface web com **Thymeleaf** quanto **API REST** com documentação OpenAPI/Swagger.

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Arquitetura](#arquitetura)
- [Funcionalidades](#funcionalidades)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [Integração Thymeleaf](#integração-thymeleaf)
- [API REST](#api-rest)
- [Documentação OpenAPI/Swagger](#documentação-openapiswagger)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Padrões de Projeto](#padrões-de-projeto)
- [Exemplos de Uso](#exemplos-de-uso)

## 🎯 Sobre o Projeto

Este projeto demonstra uma aplicação Spring Boot completa que implementa um sistema de gerenciamento de cartões de crédito com:

- **Interface Web (Thymeleaf)**: Formulários interativos para cadastro e listagem de cartões
- **API REST**: Endpoints JSON para integração com outros sistemas
- **Validação de Bandeiras**: Suporte para Visa, MasterCard e American Express
- **Padrão Strategy**: Validação específica por bandeira de cartão
- **Documentação Automática**: Swagger UI para explorar e testar a API

## 🚀 Tecnologias Utilizadas

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 21 | Linguagem de programação |
| **Spring Boot** | 4.0.0 | Framework principal |
| **Spring Web** | - | Para criação de controllers web e REST |
| **Spring Thymeleaf** | - | Template engine para páginas HTML |
| **Spring Validation** | - | Validação de dados com Bean Validation |
| **SpringDoc OpenAPI** | 3.0.0 | Geração automática de documentação API |
| **Maven** | - | Gerenciador de dependências |

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas:

```
┌─────────────────────────────────────┐
│     Camada de Apresentação          │
│  (Controllers Web + API REST)       │
├─────────────────────────────────────┤
│     Camada de Serviço               │
│  (Lógica de Negócio)                │
├─────────────────────────────────────┤
│     Camada de Domínio               │
│  (Entidades + Strategy Pattern)     │
├─────────────────────────────────────┤
│     Camada de Configuração          │
│  (OpenAPI, Exception Handlers)      │
└─────────────────────────────────────┘
```

### Componentes Principais

- **Controllers**: Gerenciam requisições HTTP (Web e API)
- **Services**: Contêm a lógica de negócio
- **Domain**: Modelos de domínio e estratégias de validação
- **Exception Handlers**: Tratamento centralizado de erros

## ✨ Funcionalidades

### Interface Web (Thymeleaf)

- ✅ Formulário de cadastro de cartões com validação
- ✅ Seleção de bandeira (Visa, MasterCard, Amex)
- ✅ Listagem de cartões registrados
- ✅ Design responsivo e moderno
- ✅ Mensagens de erro amigáveis

### API REST

- ✅ Endpoint para listar todos os cartões (`GET /api/cards`)
- ✅ Endpoint para cadastrar novo cartão (`POST /api/cards`)
- ✅ Validação automática com Bean Validation
- ✅ Respostas em formato JSON
- ✅ Tratamento de erros padronizado

### Validação de Bandeiras

- ✅ **Visa**: Inicia com 4, 16 dígitos
- ✅ **MasterCard**: Inicia com 5, 16 dígitos
- ✅ **American Express**: Inicia com 3, 15 dígitos

## 📦 Pré-requisitos

Antes de iniciar, certifique-se de ter instalado:

- **Java JDK 21** ou superior
- **Maven 3.6+**
- **Git** (opcional, para clonar o repositório)

## 🔧 Instalação e Execução

### 1. Clone o repositório (ou navegue até o diretório do projeto)

```bash
cd /home/rsantana/projetos/spring-boot/cartoes/credit-card-app
```

### 2. Compile o projeto

```bash
mvn clean install
```

### 3. Execute a aplicação

```bash
mvn spring-boot:run
```

Ou execute o JAR gerado:

```bash
java -jar target/credit-card-thymeleaf-0.0.1-SNAPSHOT.jar
```

### 4. Acesse a aplicação

A aplicação estará disponível em: **http://localhost:8080**

## 🎨 Integração Thymeleaf

### Como Funciona

O Thymeleaf é integrado através da dependência `spring-boot-starter-thymeleaf` e fornece:

1. **Templates HTML**: Localizados em `src/main/resources/templates/`
2. **Controller Web**: `CreditCardController` renderiza as views
3. **Model Binding**: Dados são passados do controller para a view via `Model`
4. **Form Binding**: Formulários HTML são vinculados a objetos Java

### Endpoints Web

| Rota | Método | Descrição | Template |
|------|--------|-----------|----------|
| `/cards/new` | GET | Exibe formulário de cadastro | `register.html` |
| `/cards` | POST | Processa cadastro de cartão | - |
| `/cards/list` | GET | Lista todos os cartões | `list.html` |

### Exemplo de Template (register.html)

```html
<form th:action="@{/cards}" th:object="${cardForm}" method="post">
    <div class="form-group">
        <label for="holderName">Nome do Titular</label>
        <input type="text" th:field="*{holderName}" />
        <span th:if="${#fields.hasErrors('holderName')}" 
              th:errors="*{holderName}" 
              class="error"></span>
    </div>
    <!-- Mais campos... -->
</form>
```

### Validação no Thymeleaf

A validação é feita através de:
- **@Valid** no controller
- **BindingResult** para capturar erros
- **th:errors** para exibir mensagens

```java
@PostMapping
public String registerCard(@Valid @ModelAttribute("cardForm") CreditCardForm form,
                          BindingResult bindingResult,
                          Model model) {
    if (bindingResult.hasErrors()) {
        return "register";
    }
    // Processar...
}
```

## 🔌 API REST

### Endpoints Disponíveis

#### 1. Listar Todos os Cartões

```http
GET /api/cards
Accept: application/json
```

**Resposta (200 OK):**
```json
[
    {
        "holderName": "João Silva",
        "number": "4111111111111111",
        "brand": "VISA"
    },
    {
        "holderName": "Maria Santos",
        "number": "5500000000000004",
        "brand": "MASTERCARD"
    }
]
```

#### 2. Cadastrar Novo Cartão

```http
POST /api/cards
Content-Type: application/json
Accept: application/json
```

**Request Body:**
```json
{
    "holderName": "Carlos Oliveira",
    "number": "4111111111111111",
    "brand": "VISA"
}
```

**Resposta (201 Created):**
```json
{
    "holderName": "Carlos Oliveira",
    "number": "4111111111111111",
    "brand": "VISA"
}
```

**Resposta de Erro (400 Bad Request):**
```json
{
    "timestamp": "2025-11-22T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Visa: número deve iniciar com 4",
    "path": "/api/cards"
}
```

### Tratamento de Erros

O `GlobalExceptionHandler` captura e trata automaticamente:

- **MethodArgumentNotValidException**: Erros de validação (@Valid)
- **IllegalArgumentException**: Erros de regras de negócio
- **CreditCardNotFoundException**: Cartão não encontrado
- **Exception**: Erros gerais

Todas as respostas de erro seguem o padrão:

```json
{
    "timestamp": "ISO-8601 DateTime",
    "status": 400,
    "error": "Tipo do Erro",
    "message": "Mensagem descritiva",
    "path": "/caminho/endpoint"
}
```

## 📚 Documentação OpenAPI/Swagger

### Acessando a Documentação

A documentação interativa está disponível em:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Configuração

A configuração do OpenAPI está em `OpenApiConfig.java`:

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Credit Cards API")
                .description("API REST para gerenciamento de cartões de crédito")
                .version("1.0.0"))
            .addServersItem(new Server()
                .url("http://localhost:8080")
                .description("Servidor de Desenvolvimento"));
    }
}
```

### Anotações Utilizadas

- **@Tag**: Agrupa endpoints por categoria
- **@Operation**: Descreve a operação
- **@ApiResponses**: Define possíveis respostas HTTP

```java
@Tag(name = "Credit Cards API", description = "API REST para gerenciamento de cartões")
@RestController
@RequestMapping("/api/cards")
public class CreditCardApiController {
    
    @Operation(summary = "Listar todos os cartões")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping
    public List<CreditCard> getAllCards() { ... }
}
```

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/example/cards/
│   │   ├── CardsApplication.java              # Classe principal
│   │   ├── config/
│   │   │   └── OpenApiConfig.java             # Configuração Swagger
│   │   ├── domain/
│   │   │   ├── CreditCard.java                # Interface do cartão
│   │   │   ├── DefaultCreditCard.java         # Implementação padrão
│   │   │   ├── CreditCardBrand.java           # Enum de bandeiras
│   │   │   ├── CreditCardBrandStrategy.java   # Interface Strategy
│   │   │   ├── CreditCardBrandFactory.java    # Factory de estratégias
│   │   │   ├── CreditCardBrandStrategyProvider.java
│   │   │   ├── visa/
│   │   │   │   └── VisaStrategy.java          # Validação Visa
│   │   │   ├── master/
│   │   │   │   └── MasterCardStrategy.java    # Validação MasterCard
│   │   │   └── amex/
│   │   │       └── AmexStrategy.java          # Validação Amex
│   │   ├── exception/
│   │   │   ├── GlobalExceptionHandler.java    # Tratamento global de erros
│   │   │   ├── ErrorResponse.java             # DTO de erro
│   │   │   └── CreditCardNotFoundException.java
│   │   ├── service/
│   │   │   └── CreditCardRegistrationService.java  # Lógica de negócio
│   │   └── web/
│   │       ├── CreditCardController.java      # Controller Thymeleaf
│   │       ├── CreditCardApiController.java   # Controller REST
│   │       ├── CreditCardForm.java            # Form DTO
│   │       └── CreditCardApiRequest.java      # API Request DTO
│   └── resources/
│       ├── application.properties             # Configurações
│       └── templates/
│           ├── register.html                  # Formulário de cadastro
│           └── list.html                      # Listagem de cartões
└── test/
    └── java/...
```

## 🎯 Padrões de Projeto

### 1. Strategy Pattern

Implementado para validação de bandeiras de cartão:

```java
public interface CreditCardBrandStrategy {
    void validate(String number);
    String normalize(String number);
    double calculateFee(double amount);
}

// Implementações específicas
class VisaStrategy implements CreditCardBrandStrategy { ... }
class MasterCardStrategy implements CreditCardBrandStrategy { ... }
class AmexStrategy implements CreditCardBrandStrategy { ... }
```

**Benefícios:**
- Facilita adição de novas bandeiras
- Separa lógica de validação por bandeira
- Código mais limpo e manutenível

### 2. Factory Pattern

`CreditCardBrandFactory` cria instâncias de estratégias:

```java
public class CreditCardBrandFactory {
    public static CreditCardBrandStrategy getStrategy(CreditCardBrand brand) {
        return switch(brand) {
            case VISA -> new VisaStrategy();
            case MASTERCARD -> new MasterCardStrategy();
            case AMEX -> new AmexStrategy();
        };
    }
}
```

### 3. DTO Pattern

Separação entre entidades de domínio e objetos de transferência:

- **CreditCardForm**: Para formulários web
- **CreditCardApiRequest**: Para requisições API
- **ErrorResponse**: Para respostas de erro

## 💡 Exemplos de Uso

### Exemplo 1: Testar via Swagger UI

1. Acesse: http://localhost:8080/swagger-ui.html
2. Expanda o endpoint `POST /api/cards`
3. Clique em "Try it out"
4. Insira o JSON:
```json
{
    "holderName": "João Silva",
    "number": "4111111111111111",
    "brand": "VISA"
}
```
5. Clique em "Execute"

### Exemplo 2: Testar via cURL

```bash
# Listar cartões
curl -X GET http://localhost:8080/api/cards

# Cadastrar cartão Visa
curl -X POST http://localhost:8080/api/cards \
  -H "Content-Type: application/json" \
  -d '{
    "holderName": "João Silva",
    "number": "4111111111111111",
    "brand": "VISA"
  }'

# Cadastrar cartão MasterCard
curl -X POST http://localhost:8080/api/cards \
  -H "Content-Type: application/json" \
  -d '{
    "holderName": "Maria Santos",
    "number": "5500000000000004",
    "brand": "MASTERCARD"
  }'

# Cadastrar cartão Amex
curl -X POST http://localhost:8080/api/cards \
  -H "Content-Type: application/json" \
  -d '{
    "holderName": "Carlos Oliveira",
    "number": "340000000000009",
    "brand": "AMEX"
  }'
```

### Exemplo 3: Usar Interface Web

1. Acesse: http://localhost:8080/cards/new
2. Preencha o formulário:
   - Nome do Titular: João Silva
   - Número do Cartão: 4111111111111111
   - Bandeira: VISA
3. Clique em "Registrar Cartão"
4. Será redirecionado para a lista de cartões

### Exemplo 4: Teste de Validação

Tentando cadastrar cartão inválido:

```bash
curl -X POST http://localhost:8080/api/cards \
  -H "Content-Type: application/json" \
  -d '{
    "holderName": "Teste",
    "number": "1234567890123456",
    "brand": "VISA"
  }'
```

Resposta esperada (400 Bad Request):
```json
{
    "timestamp": "2025-11-22T10:30:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Visa: número deve iniciar com 4",
    "path": "/api/cards"
}
```

## 🔒 Segurança e Boas Práticas

- ✅ Validação de entrada com Bean Validation
- ✅ Tratamento centralizado de exceções
- ✅ Separação de camadas (MVC)
- ✅ Uso de interfaces para desacoplamento
- ✅ Padrões de projeto para flexibilidade
- ⚠️ **Nota**: Este é um projeto de demonstração. Para produção, considere:
  - Adicionar autenticação/autorização (Spring Security)
  - Implementar persistência real (JPA/Hibernate)
  - Adicionar testes unitários e de integração
  - Implementar HTTPS
  - Mascarar números de cartão nos logs

## 📝 Configurações

### application.properties

```properties
# Porta do servidor
server.port=8080

# Thymeleaf (desabilita cache em dev)
spring.thymeleaf.cache=false

# SpringDoc OpenAPI
# springdoc.api-docs.path=/v3/api-docs
# springdoc.swagger-ui.path=/swagger-ui.html
# springdoc.swagger-ui.operationsSorter=method
# springdoc.swagger-ui.tagsSorter=alpha
```

## 🚧 Melhorias Futuras

- [ ] Persistência com banco de dados (PostgreSQL/MySQL)
- [ ] Implementar algoritmo de Luhn para validação completa
- [ ] Adicionar autenticação JWT
- [ ] Criar testes unitários e de integração
- [ ] Implementar paginação na listagem
- [ ] Adicionar busca e filtros
- [ ] Criar dashboard com estatísticas
- [ ] Implementar soft delete
- [ ] Adicionar auditoria de operações
- [ ] Dockerizar a aplicação

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto é licenciado sob a MIT License.

## 👨‍💻 Autor

Desenvolvido por **Reinaldo Jesus Santana**

- Email: reinaldojsantana@gmail.com
- linkdIn: https://www.linkedin.com/in/reinaldo-jesus-santana-09079814/

## 📞 Suporte

Para questões e suporte:

- Abra uma issue no repositório
- Entre em contato via email: reinaldojsantana@gmail.com

---

⭐ Se este projeto foi útil para você, considere dar uma estrela!

**Desenvolvido com ❤️ usando Spring Boot e Thymeleaf**
