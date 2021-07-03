package com.pedrogobira.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class NegativeStockException extends RuntimeException {

    public NegativeStockException() {
        super("Stock quantity can't be negative");
    }
}
