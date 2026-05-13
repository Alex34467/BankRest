package com.example.bankcards.exception;

public class InvalidCardExpireDateException extends RuntimeException {

    public InvalidCardExpireDateException(Throwable cause) {
        super(cause);
    }
}
