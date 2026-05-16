package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDetailsDto;
import com.example.bankcards.model.CardDto;
import com.example.bankcards.model.MoneyTransferRequest;
import com.example.bankcards.model.MoneyTransferResponse;
import com.example.bankcards.service.CardService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    @Autowired
    private CardService cardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CardDto> getAllCards() {
        return cardService.findAllCards();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto findById(@PathVariable Long id) {
        return cardService.findById(id);
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CardDto> findByOwner(@PathVariable Long ownerId) {
        return cardService.findCardsByOwner(ownerId);
    }

    @GetMapping("/owner/search")
    @PreAuthorize("hasRole('USER')")
    public List<CardDto> findCurrentUserCardsByName(
            @AuthenticationPrincipal UserDetailsDto user,
            @RequestParam("searchNumber") String searchNumber,
            Pageable pageable
    ) {
        return cardService.searchCardsByOwnerAndNumber(user.getId(), searchNumber, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CardDto saveCard(@RequestBody CardDto cardDto) {
        return cardService.saveCard(cardDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public MoneyTransferResponse makeTransferBetweenCards(
            @AuthenticationPrincipal UserDetailsDto user,
            @RequestBody MoneyTransferRequest request
    ) {
        Pair<CardDto, CardDto> cards = cardService.transferMoney(
                user.getId(),
                request.getSourceCardNumber(), request.getTargetCardNumber(),
                request.getAmount()
        );

        return new MoneyTransferResponse(
                cards.getLeft().getNumber(),
                cards.getLeft().getBalance(),
                cards.getRight().getNumber(),
                cards.getRight().getBalance()
        );
    }
}
