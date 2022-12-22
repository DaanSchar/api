package com.voidhub.api.exceptions.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;

@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(
                new fieldErrorMessage("Invalid request", getErrorsAsMap(ex)),
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    private static class fieldErrorMessage {
        @JsonProperty("message")
        String message;
        @JsonProperty("errors")
        HashMap<String, String> errors;

        public fieldErrorMessage(String message, HashMap<String, String>  errors) {
            this.message = message;
            this.errors = errors;
        }
    }

    private HashMap<String, String> getErrorsAsMap(MethodArgumentNotValidException ex) {
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        HashMap<String, String> errorMap = new HashMap<>();

        for (ObjectError error : errors) {
            errorMap.put(((FieldError) error).getField(), error.getDefaultMessage());
        }

        return errorMap;
    }

}
