package com.seulmae.seulmae.global.exception;

public class InvalidAccountIdException extends IllegalArgumentException {
    public InvalidAccountIdException(String message) {
        super(message);
    }
}
