package com.example.bankcards.controller;

import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardNumberNotUniqueException;
import com.example.bankcards.exception.InsufficientCardBalanceException;
import com.example.bankcards.exception.InvalidCardExpireDateException;
import com.example.bankcards.exception.InvalidCardNumberException;
import com.example.bankcards.exception.InvalidCardOwnerException;
import com.example.bankcards.exception.InvalidCardStatusException;
import com.example.bankcards.exception.TransferWithSameCardException;
import com.example.bankcards.exception.UserEmailNotUniqueException;
import com.example.bankcards.exception.UserNameNotUniqueException;
import com.example.bankcards.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BankRestAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
    }

    @ExceptionHandler(UserNameNotUniqueException.class)
    public ResponseEntity<String> handleUsernameNotUnique() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с таким именем уже существует");
    }

    @ExceptionHandler(UserEmailNotUniqueException.class)
    public ResponseEntity<String> handleEmailNotUnique() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Пользователь с такой почтой уже существует");
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<String> handleCardNotFound() {
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

    @ExceptionHandler(CardNumberNotUniqueException.class)
    public ResponseEntity<String> handleCardNumberNotUnique() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Карта с таким номером уже существует");
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
