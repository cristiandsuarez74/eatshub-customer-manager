package com.estudio.customer_manager.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandlerController {
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String,Object>>> handlerValidationErrors(WebExchangeBindException exception){
        final Map<String,Object> errors=new HashMap<>();
        errors.put("status", HttpStatus.BAD_REQUEST);
        errors.put("message","validation failed");
        final Map<String,String> fieldErrors=new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            fieldErrors.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        errors.put("fieldErrors",fieldErrors);
        return Mono.just(ResponseEntity.badRequest().body(errors));


    }
}
