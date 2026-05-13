package com.example.bankcards.util;

import com.example.bankcards.dto.UserDetailsDto;
import com.example.bankcards.entity.CardEntity;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.UserRole;
import com.example.bankcards.model.AuthRequest;
import com.example.bankcards.model.CardDto;
import com.example.bankcards.model.MoneyTransferRequest;
import com.example.bankcards.model.UserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class TestUtil {

    public static List<CardEntity> getCardEntities() {
        return List.of(getCardEntity());
    }

    public static CardEntity getCardEntity() {
        return new CardEntity(1L,
                "1234123412341234",
                1L,
                YearMonth.of(2025, 11),
                CardStatus.ACTIVE,
                BigDecimal.ZERO
        );
    }

    public static CardEntity getCardEntity(Long id, String numberSuffix, BigDecimal balance) {
        return new CardEntity(id,
                "1234123412341234".substring(0, 15 - numberSuffix.length()) + numberSuffix,
                1L,
                YearMonth.of(2026, 11),
                CardStatus.ACTIVE,
                balance
        );
    }

    public static CardDto getCardDto(Long id) {
        return new CardDto(id,
                "1234123412341234",
                1L,
                "11/26",
                CardDto.StatusEnum.ACTIVE,
                BigDecimal.ZERO
        );
    }

    public static List<CardDto> getCardsDtos() {
        return List.of(TestUtil.getCardDto(1L));
    }

    public static MoneyTransferRequest getMoneyTransferRequest(String sourceCardNumberSuffix, String targetCardNumberSuffix) {
        return new MoneyTransferRequest(
                "1234123412341234".substring(0, 15 - sourceCardNumberSuffix.length()) + sourceCardNumberSuffix,
                "1234123412341234".substring(0, 15 - targetCardNumberSuffix.length()) + targetCardNumberSuffix,
                BigDecimal.TEN
        );
    }

    public static List<UserEntity> getUserEntities() {
        return List.of(getUserEntity());
    }

    public static UserEntity getUserEntity() {
        return new UserEntity(
                1L,
                "username",
                "email",
                "password",
                UserRole.USER
        );
    }

    public static UserDto getUserDto() {
        return new UserDto(
                1L,
                "username",
                "email",
                "password",
                UserDto.RoleEnum.USER
        );
    }

    public static List<UserDto> getUsersDtos() {
        return List.of(getUserDto());
    }

    public static UserDetails getUserDetails() {
        return new UserDetailsDto(
                1L,
                "username",
                "email",
                "password",
                UserDto.RoleEnum.USER
        );
    }

    public static AuthRequest getAuthRequest() {
        return new AuthRequest("Admin", "123456");
    }

    public static Authentication getAuthentification() {
        return new UsernamePasswordAuthenticationToken(
                "Admin",
                "123456"
        );
    }
}
