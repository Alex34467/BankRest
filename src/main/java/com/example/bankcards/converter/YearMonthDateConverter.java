package com.example.bankcards.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

@Converter(autoApply = true)
public class YearMonthDateConverter implements AttributeConverter<YearMonth, LocalDate> {

    @Override
    public LocalDate convertToDatabaseColumn(YearMonth yearMonth) {
        return (Objects.isNull(yearMonth)) ? null : yearMonth.atDay(1);
    }

    @Override
    public YearMonth convertToEntityAttribute(LocalDate date) {
        return (Objects.isNull(date)) ? null : YearMonth.of(date.getYear(), date.getMonth());
    }
}
