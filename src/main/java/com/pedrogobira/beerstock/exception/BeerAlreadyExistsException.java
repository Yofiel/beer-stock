package com.pedrogobira.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerAlreadyExistsException extends RuntimeException {

    public BeerAlreadyExistsException(String name){
        super("Beer " + name + " already exists");
    }
}
