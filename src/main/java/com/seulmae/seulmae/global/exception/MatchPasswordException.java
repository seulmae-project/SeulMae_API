package com.seulmae.seulmae.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MatchPasswordException extends RuntimeException{
    public MatchPasswordException(String message) {
        super(message);
    }
}
