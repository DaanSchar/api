package com.voidhub.api.exceptions.handler;

import com.voidhub.api.exceptions.InvalidFieldException;
import com.voidhub.api.util.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FieldExceptionHandler {

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity<Message> handleInvalidFieldException(InvalidFieldException ex) {
        return new ResponseEntity<>(new Message(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Message> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(new Message(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Message> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
                new Message(ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
