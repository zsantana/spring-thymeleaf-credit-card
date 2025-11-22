package com.example.cards.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

/**
 * Manipulador global de exceções para APIs REST
 */
@RestControllerAdvice
public class GlobalExceptionHandler{

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata erros de validação de campos (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(
        responseCode = "400",
        description = "Erro de validação nos dados de entrada",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .message("Dados de entrada inválidos")
                .path(request.getDescription(false).replace("uri=", ""))
                .fieldErrors(fieldErrors)
                .build();

        logger.error("Validation error occurred: {}", errorResponse, ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Trata erros de argumento ilegal (regras de negócio)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponse(
        responseCode = "400",
        description = "Erro nas regras de negócio",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Business Rule Error")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        logger.error("Business rule error occurred: {}", errorResponse, ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Trata erros de deserialização de JSON (ex: valores inválidos para enum)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ApiResponse(
        responseCode = "400",
        description = "Erro ao processar o JSON da requisição",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        String message = "Formato de dados inválido no JSON";
        
        // Extrai mensagem mais específica se disponível
        if (ex.getCause() != null) {
            String causeMessage = ex.getCause().getMessage();
            if (causeMessage != null && causeMessage.contains("CreditCardBrand")) {
                message = "Valor inválido para bandeira do cartão. Valores aceitos: VISA, MASTERCARD, AMEX";
            } else if (causeMessage != null) {
                message = "Erro ao processar o JSON: " + causeMessage;
            }
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("JSON Parse Error")
                .message(message)
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        logger.error("JSON parse error occurred: {} {}", message);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * Trata exceções genéricas não mapeadas
     */
    @ExceptionHandler(Exception.class)
    @ApiResponse(
        responseCode = "500",
        description = "Erro interno do servidor",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocorreu um erro interno. Tente novamente mais tarde.")
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        logger.error("Unhandled exception occurred: {}", errorResponse, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Trata erros de cartão não encontrado (se necessário no futuro)
     */
    @ExceptionHandler(CreditCardNotFoundException.class)
    @ApiResponse(
        responseCode = "404",
        description = "Cartão não encontrado",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public ResponseEntity<ErrorResponse> handleCreditCardNotFoundException(
            CreditCardNotFoundException ex, WebRequest request) {
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();

        logger.error("Credit card not found: {}", errorResponse, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
}
