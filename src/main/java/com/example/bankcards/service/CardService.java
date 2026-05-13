package com.example.bankcards.service;

import com.example.bankcards.converter.CardConverter;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardNumberNotUniqueException;
import com.example.bankcards.exception.InsufficientCardBalanceException;
import com.example.bankcards.exception.InvalidCardExpireDateException;
import com.example.bankcards.exception.InvalidCardNumberException;
import com.example.bankcards.exception.InvalidCardOwnerException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.exception.TransferWithSameCardException;
import com.example.bankcards.model.CardDto;
import com.example.bankcards.repository.CardRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserService userService;

    public List<CardDto> findAllCards() {
        return CardConverter.toDtos(cardRepository.findAll());
    }

    public CardDto findById(Long id) {
        try {
            return CardConverter.toDto(cardRepository.getReferenceById(id));
        } catch (EntityNotFoundException e) {
            throw new CardNotFoundException(e);
        }
    }

    public List<CardDto> findCardsByOwner(Long ownerId) {
        return CardConverter.toDtos(cardRepository.findByOwnerId(ownerId));
    }

    public List<CardDto> searchCardsByOwnerAndNumber(Long ownerId, String searchNumber, Pageable pageable) {
        return CardConverter.toDtos(cardRepository.findByOwnerIdAndNumberContaining(ownerId, searchNumber, pageable));
    }

    public CardDto saveCard(CardDto cardDto) {
        validateCard(cardDto);
        CardEntity entity = CardConverter.toEntity(cardDto);
        cardRepository.save(entity);
        return CardConverter.toDto(entity);
    }

    public void deleteCard(Long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
        } else {
            throw new CardNotFoundException();
        }
    }

    @Transactional
    public Pair<CardDto, CardDto> transferMoney(Long userId, String sourceCardNumber, String targetCardNumber, BigDecimal amount) {
        if (sourceCardNumber.equals(targetCardNumber)) {
            throw new TransferWithSameCardException();
        }

        Optional<CardEntity> sourceCardOptional = cardRepository.findByNumber(sourceCardNumber);
        Optional<CardEntity> targetCardOptional = cardRepository.findByNumber(targetCardNumber);
        if (sourceCardOptional.isEmpty() || targetCardOptional.isEmpty()) {
            throw new CardNotFoundException();
        }

        CardEntity sourceCard = sourceCardOptional.get();
        CardEntity targetCard = targetCardOptional.get();
        if (!sourceCard.getOwnerId().equals(userId) || !targetCard.getOwnerId().equals(userId)) {
            throw new InvalidCardOwnerException();
        }
        if (!sourceCard.getStatus().equals(CardStatus.ACTIVE)
                || sourceCard.getExpirationDate().isBefore(YearMonth.now())
                || !targetCard.getStatus().equals(CardStatus.ACTIVE)
                || targetCard.getExpirationDate().isBefore(YearMonth.now())) {
            throw new InvalidCardStatusException();
        }
        if (sourceCard.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientCardBalanceException();
        }

        sourceCard.setBalance(sourceCard.getBalance().subtract(amount));
        targetCard.setBalance(targetCard.getBalance().add(amount));
        cardRepository.save(sourceCard);
        cardRepository.save(targetCard);
        return Pair.of(CardConverter.toDto(sourceCard), CardConverter.toDto(targetCard));
    }

    public void validateCard(CardDto card) {
        if (!Objects.isNull(card.getId()) && !cardRepository.existsById(card.getId())) {
            throw new CardNotFoundException();
        }
        validateCardNumber(card);
        if (!userService.existsById(card.getOwnerId())) {
            throw new InvalidCardOwnerException();
        }
        try {
            YearMonth expiredDate = YearMonth.parse(card.getExpirationDate(), DateTimeFormatter.ofPattern("MM/yy"));
            if (expiredDate.isBefore(YearMonth.now()) && card.getStatus() == CardDto.StatusEnum.ACTIVE) {
                throw new InvalidCardStatusException();
            }
        } catch (DateTimeParseException e) {
            throw new InvalidCardExpireDateException(e);
        }
    }

    public void validateCardNumber(CardDto card) {
        if (!card.getNumber().matches("^\\d{16}$")) {
            throw new InvalidCardNumberException();
        }
        Optional<CardEntity> otherCardEntity = cardRepository.findByNumber(card.getNumber());
        if (otherCardEntity.isPresent()) {
            if (!otherCardEntity.get().getId().equals(card.getId())) {
                throw new CardNumberNotUniqueException();
            }
        }
    }
}
