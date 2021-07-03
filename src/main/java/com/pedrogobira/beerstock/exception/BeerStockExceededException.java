package com.pedrogobira.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BeerStockExceededException extends RuntimeException {

    public BeerStockExceededException(){
        super("Beer stock exceeded");
    }
}
