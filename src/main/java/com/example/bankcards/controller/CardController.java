package com.example.bankcards.controller;

import com.example.bankcards.dto.UserDetailsDto;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientCardBalanceException;
import com.example.bankcards.exception.InvalidCardExpireDateException;
import com.example.bankcards.exception.InvalidCardNumberException;
import com.example.bankcards.exception.InvalidCardOwnerException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.exception.TransferWithSameCardException;
import com.example.bankcards.model.CardDto;
import com.example.bankcards.model.MoneyTransferRequest;
import com.example.bankcards.model.MoneyTransferResponse;
import com.example.bankcards.service.CardService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Карта не найдена");
    }

    @ExceptionHandler(InvalidCardOwnerException.class)
    public ResponseEntity<String> handleInvalidCardOwner() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректный владелец карты");
    }

    @ExceptionHandler(InvalidCardNumberException.class)
    public ResponseEntity<String> handleInvalidCardNumber() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректный номер карты");
    }

    @ExceptionHandler(InvalidCardStatusException.class)
    public ResponseEntity<String> handleInvalidCardStatus() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректный статус карты");
    }

    @ExceptionHandler(InvalidCardExpireDateException.class)
    public ResponseEntity<String> handleInvalidCardExpireDate() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Некорректный срок действия карты");
    }

    @ExceptionHandler(InsufficientCardBalanceException.class)
    public ResponseEntity<String> handleInsufficientCardBalance() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("На карте недостаточно средств для проведения операции");
    }

    @ExceptionHandler(TransferWithSameCardException.class)
    public ResponseEntity<String> handleTransferWithSameCard() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("В переводе должны участвовать разные карты");
    }
}
