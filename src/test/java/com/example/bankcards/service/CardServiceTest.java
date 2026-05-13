package com.example.bankcards.service;

import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientCardBalanceException;
import com.example.bankcards.exception.InvalidCardOwnerException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.exception.TransferWithSameCardException;
import com.example.bankcards.model.CardDto;
import com.example.bankcards.model.MoneyTransferRequest;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.TestUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private CardService cardService;

    @Test
    void findAllCardsTest_success() {
        Mockito.when(cardRepository.findAll())
                .thenReturn(TestUtil.getCardEntities());

        List<CardDto> cards = cardService.findAllCards();

        assertEquals(1, cards.size());
        Mockito.verify(cardRepository, Mockito.times(1)).findAll();
    }

    @Test
    void findByIdTest_success() {
        Mockito.when(cardRepository.getReferenceById(anyLong()))
                .thenReturn(TestUtil.getCardEntity());

        CardDto card = cardService.findById(1L);

        assertNotNull(card);
        Mockito.verify(cardRepository, Mockito.times(1)).getReferenceById(anyLong());
    }

    @Test
    void findCardsByOwnerTest_success() {
        Mockito.when(cardRepository.findByOwnerId(anyLong()))
                .thenReturn(TestUtil.getCardEntities());

        List<CardDto> cards = cardService.findCardsByOwner(1L);

        assertEquals(1, cards.size());
        Mockito.verify(cardRepository, Mockito.times(1)).findByOwnerId(anyLong());
    }

    @Test
    void searchCardsByOwnerAndNumberTest_success() {
        Mockito.when(cardRepository.findByOwnerIdAndNumberContaining(anyLong(), anyString(), any()))
                .thenReturn(TestUtil.getCardEntities());

        List<CardDto> cards = cardService.searchCardsByOwnerAndNumber(1L, "1234", Pageable.ofSize(1));

        assertEquals(1, cards.size());
        Mockito.verify(cardRepository, Mockito.times(1)).findByOwnerIdAndNumberContaining(anyLong(), anyString(), any());
    }

    @Test
    void saveCardTest_success() {
        Mockito.when(cardRepository.save(any()))
                .thenReturn(TestUtil.getCardEntity());
        Mockito.when(userService.existsById(anyLong()))
                .thenReturn(true);

        CardDto savedCard = cardService.saveCard(TestUtil.getCardDto(null));

        assertNotNull(savedCard);
        Mockito.verify(cardRepository, Mockito.times(1)).save(any());
    }

    @Test
    void deleteCardTest_success() {
        Mockito.when(cardRepository.existsById(anyLong()))
                        .thenReturn(true);

        cardService.deleteCard(1L);

        Mockito.verify(cardRepository, Mockito.times(1)).existsById(anyLong());
        Mockito.verify(cardRepository, Mockito.times(1)).deleteById(anyLong());
    }

    @Test
    void transferMoneyTest_success() {
        MoneyTransferRequest request = TestUtil.getMoneyTransferRequest("11", "22");
        Mockito.when(cardRepository.findByNumber(request.getSourceCardNumber()))
                .thenReturn(Optional.of(TestUtil.getCardEntity(1L, "11", BigDecimal.TEN)));
        Mockito.when(cardRepository.findByNumber(request.getTargetCardNumber()))
                .thenReturn(Optional.of(TestUtil.getCardEntity(2L, "22", BigDecimal.ZERO)));

        Pair<CardDto, CardDto> result = cardService.transferMoney(1L, request.getSourceCardNumber(), request.getTargetCardNumber(), request.getAmount());

        assertNotNull(result.getLeft());
        assertNotNull(result.getRight());
        Mockito.verify(cardRepository, Mockito.times(2)).findByNumber(anyString());
        Mockito.verify(cardRepository, Mockito.times(2)).save(any());
    }

    @Test
    void transferMoneyTest_failureTransferWithSameCard() {
        MoneyTransferRequest request = TestUtil.getMoneyTransferRequest("11", "11");

        assertThrows(TransferWithSameCardException.class, () ->
                cardService.transferMoney(1L, request.getSourceCardNumber(), request.getTargetCardNumber(), request.getAmount()));
    }

    @Test
    void transferMoneyTest_failureCardNotFound() {
        MoneyTransferRequest request = TestUtil.getMoneyTransferRequest("11", "22");
        Mockito.when(cardRepository.findByNumber(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () ->
                cardService.transferMoney(1L, request.getSourceCardNumber(), request.getTargetCardNumber(), request.getAmount()));
    }

    @Test
    void transferMoneyTest_failureInvalidCardOwner() {
        MoneyTransferRequest request = TestUtil.getMoneyTransferRequest("11", "22");
        Mockito.when(cardRepository.findByNumber(request.getSourceCardNumber()))
                .thenReturn(Optional.of(TestUtil.getCardEntity(1L, "11", BigDecimal.TEN)));
        Mockito.when(cardRepository.findByNumber(request.getTargetCardNumber()))
                .thenReturn(Optional.of(TestUtil.getCardEntity(2L, "22", BigDecimal.ZERO)));

        assertThrows(InvalidCardOwnerException.class, () ->
                cardService.transferMoney(2L, request.getSourceCardNumber(), request.getTargetCardNumber(), request.getAmount()));
    }

    @Test
    void transferMoneyTest_failureInvalidCardStatus() {
        MoneyTransferRequest request = TestUtil.getMoneyTransferRequest("11", "22");
        Mockito.when(cardRepository.findByNumber(request.getSourceCardNumber()))
                .thenReturn(Optional.of(TestUtil.getCardEntity(1L, "11", BigDecimal.TEN)));
        Mockito.when(cardRepository.findByNumber(request.getTargetCardNumber()))
                .thenReturn(Optional.of(TestUtil.getCardEntity()));

        assertThrows(InvalidCardStatusException.class, () ->
                cardService.transferMoney(1L, request.getSourceCardNumber(), request.getTargetCardNumber(), request.getAmount()));
    }

    @Test
    void transferMoneyTest_failureInsufficientCardBalance() {
        MoneyTransferRequest request = TestUtil.getMoneyTransferRequest("11", "22");
        Mockito.when(cardRepository.findByNumber(request.getSourceCardNumber()))
                .thenReturn(Optional.of(TestUtil.getCardEntity(1L, "11", BigDecimal.ZERO)));
        Mockito.when(cardRepository.findByNumber(request.getTargetCardNumber()))
                .thenReturn(Optional.of(TestUtil.getCardEntity(2L, "22", BigDecimal.ZERO)));

        assertThrows(InsufficientCardBalanceException.class, () ->
                cardService.transferMoney(1L, request.getSourceCardNumber(), request.getTargetCardNumber(), request.getAmount()));
    }
}