package com.seulmae.seulmae.user.exception;

public class InvalidAccountIdException extends RuntimeException {
    public InvalidAccountIdException(String message) {
        super(message);
    }
}
