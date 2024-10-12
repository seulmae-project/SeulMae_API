package com.seulmae.seulmae.global.exception;

public class InvalidAccountIdException extends RuntimeException {
    public InvalidAccountIdException(String message) {
        super(message);
    }
}
