package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;

@Entity
@Table(name = "cards")
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private Long ownerId;

    @Column(name = "expiration_date")
    private YearMonth expirationDate;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::card_status")
    private CardStatus status;

    private BigDecimal balance;

    public CardEntity() {
    }

    public CardEntity(Long id, String number, Long ownerId, YearMonth expirationDate, CardStatus status, BigDecimal balance) {
        this.id = id;
        this.number = number;
        this.ownerId = ownerId;
        this.expirationDate = expirationDate;
        this.status = status;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public YearMonth getExpirationDate() {
        return expirationDate;
    }

    public CardStatus getStatus() {
        return status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
