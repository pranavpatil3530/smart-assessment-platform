package com.smartassessment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExamAlreadySubmittedException extends RuntimeException {
    public ExamAlreadySubmittedException(String message) {
        super(message);
    }
}
