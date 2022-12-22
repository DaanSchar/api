package com.voidhub.api.exceptions.handler;

import com.voidhub.api.exceptions.EntityAlreadyExistsException;
import com.voidhub.api.util.Message;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Message> handleEntityNotFoundException(EntityNotFoundException ex) {
        return new ResponseEntity<>(new Message(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Message> handleEntityAlreadyExistsException(EntityAlreadyExistsException ex) {
        return new ResponseEntity<>(new Message(ex.getMessage()), HttpStatus.CONFLICT);
    }

}
