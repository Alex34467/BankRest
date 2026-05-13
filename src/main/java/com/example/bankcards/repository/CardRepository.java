package com.example.bankcards.repository;

import com.example.bankcards.entity.CardEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, Long> {

    Optional<CardEntity> findByNumber(String number);

    List<CardEntity> findByOwnerId(Long ownerId);

    List<CardEntity> findByOwnerIdAndNumberContaining(Long ownerId, String number, Pageable pageable);
}
