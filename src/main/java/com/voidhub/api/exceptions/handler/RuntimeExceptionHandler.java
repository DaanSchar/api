package com.voidhub.api.exceptions.handler;

import com.voidhub.api.util.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RuntimeExceptionHandler {

//    @ExceptionHandler(RuntimeException.class)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Message> handleRuntimeException(RuntimeException ex) {
        return new ResponseEntity<>(new Message(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

}
