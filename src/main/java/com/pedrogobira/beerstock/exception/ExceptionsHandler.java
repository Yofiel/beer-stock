package com.pedrogobira.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler(BeerAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleException(BeerAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler(BeerStockExceededException.class)
    public ResponseEntity<ExceptionResponse> handleException(BeerStockExceededException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ExceptionResponse(exception.getMessage()));
    }

    @ExceptionHandler(NegativeStockException.class)
    public ResponseEntity<ExceptionResponse> handleException(NegativeStockException exception) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ExceptionResponse(exception.getMessage()));
    }
}
