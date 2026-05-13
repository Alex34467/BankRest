package com.example.bankcards.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
