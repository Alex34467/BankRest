package com.example.bankcards.converter;

import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.model.CardDto;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class CardConverter {
    public static CardDto toDto(CardEntity entity) {
        return new CardDto(
                entity.getId(),
                entity.getNumber().replaceAll(".(?=.{4})", "*"),
                entity.getOwnerId(),
                entity.getExpirationDate().toString(),
                CardDto.StatusEnum.fromValue(entity.getStatus().name()),
                entity.getBalance()
        );
    }

    public static List<CardDto> toDtos(Collection<CardEntity> entities) {
        return entities.stream()
                .map(CardConverter::toDto)
                .toList();
    }

    public static CardEntity toEntity(CardDto dto) {
        return new CardEntity(
                dto.getId(),
                dto.getNumber(),
                dto.getOwnerId(),
                YearMonth.parse(dto.getExpirationDate(), DateTimeFormatter.ofPattern("MM/yy")),
                CardStatus.valueOf(dto.getStatus().name()),
                dto.getBalance()
        );
    }
}
