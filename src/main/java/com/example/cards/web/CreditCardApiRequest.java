package com.example.cards.web;

import com.example.cards.domain.CreditCardBrand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request para registro de cartão de crédito via API REST
 */
@Schema(description = "Dados para registro de cartão de crédito via API")
public class CreditCardApiRequest {

    @Schema(description = "Nome completo do titular do cartão", example = "João Silva Santos")
    @NotBlank(message = "Nome do titular é obrigatório")
    private String holderName;

    @Schema(description = "Número do cartão de crédito (sem espaços ou traços)", 
            example = "4111111111111111")
    @NotBlank(message = "Número do cartão é obrigatório")
    private String number;

    @Schema(description = "Bandeira do cartão de crédito", 
            example = "VISA", 
            allowableValues = {"VISA", "MASTERCARD", "AMERICAN_EXPRESS"})
    @NotNull(message = "Bandeira é obrigatória")
    private CreditCardBrand brand;

    // Construtor vazio (necessário para Jackson)
    public CreditCardApiRequest() {}

    // Construtor completo
    public CreditCardApiRequest(String holderName, String number, CreditCardBrand brand) {
        this.holderName = holderName;
        this.number = number;
        this.brand = brand;
    }

    // Getters e Setters
    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public CreditCardBrand getBrand() {
        return brand;
    }

    public void setBrand(CreditCardBrand brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "CreditCardApiRequest{" +
                "holderName='" + holderName + '\'' +
                ", number='***masked***'" +  // Não expor número completo em logs
                ", brand=" + brand +
                '}';
    }
}
