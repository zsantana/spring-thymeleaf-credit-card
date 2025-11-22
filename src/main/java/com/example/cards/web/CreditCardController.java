package com.example.cards.web;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.cards.domain.CreditCard;
import com.example.cards.domain.DefaultCreditCard;
import com.example.cards.service.CreditCardRegistrationService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cards")
public class CreditCardController {

    private final CreditCardRegistrationService service;

    public CreditCardController(CreditCardRegistrationService service) {
        this.service = service;
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("cardForm", new CreditCardForm());
        model.addAttribute("brands", com.example.cards.domain.CreditCardBrand.values());
        return "register";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String registerCard(@Valid @ModelAttribute("cardForm") CreditCardForm form,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("brands", com.example.cards.domain.CreditCardBrand.values());
            return "register";
        }

        try {
            CreditCard card = new DefaultCreditCard(
                    form.getHolderName(),
                    form.getNumber(),
                    form.getBrand()
            );

            service.register(card);
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("number", "error.number", ex.getMessage());
            model.addAttribute("brands", com.example.cards.domain.CreditCardBrand.values());
            return "register";
        }

        return "redirect:/cards/list";
    }

    @GetMapping("/list")
    public String listCards(Model model) {
        model.addAttribute("cards", service.getAllCards());
        return "list";
    }

}