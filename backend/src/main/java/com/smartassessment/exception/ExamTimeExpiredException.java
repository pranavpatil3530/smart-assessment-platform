package com.smartassessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExamTimeExpiredException extends RuntimeException {
    public ExamTimeExpiredException(String message) {
        super(message);
    }
}
