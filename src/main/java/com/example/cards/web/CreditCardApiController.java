package com.example.cards.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cards.domain.CreditCard;
import com.example.cards.domain.DefaultCreditCard;
import com.example.cards.service.CreditCardRegistrationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller REST para gerenciar cartões de crédito via API JSON
 */
@RestController
@RequestMapping("/api/cards")
@Tag(name = "Credit Cards API", description = "API REST para gerenciamento de cartões de crédito")
public class CreditCardApiController {

    private final CreditCardRegistrationService service;

    public CreditCardApiController(CreditCardRegistrationService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos os cartões", description = "Retorna a lista completa de cartões registrados no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de cartões retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CreditCard> getAllCards() {
        return service.getAllCards();
    }

    @Operation(summary = "Registrar novo cartão", description = "Registra um novo cartão de crédito no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cartão registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou erro de validação"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreditCard> createCard(@Valid @RequestBody CreditCardApiRequest request) {
        // O GlobalExceptionHandler trata automaticamente:
        // - MethodArgumentNotValidException (@Valid)
        // - IllegalArgumentException (regras de negócio)
        
        CreditCard creditCard = new DefaultCreditCard(
                request.getHolderName(),
                request.getNumber(), 
                request.getBrand()
        );
        
        service.register(creditCard);
        return ResponseEntity.status(HttpStatus.CREATED).body(creditCard);
    }
}
