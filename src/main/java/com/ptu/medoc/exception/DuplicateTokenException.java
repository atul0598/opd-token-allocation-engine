package com.ptu.medoc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateTokenException extends RuntimeException {
    public DuplicateTokenException(String s) {
        super(s);
    }
}
