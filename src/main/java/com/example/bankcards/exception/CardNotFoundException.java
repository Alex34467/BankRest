package com.example.bankcards.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException() {
    }

    public CardNotFoundException(Throwable cause) {
        super(cause);
    }
}
