package com.pedrogobira.beerstock.controller;

import com.pedrogobira.beerstock.dto.BeerDto;
import com.pedrogobira.beerstock.dto.QuantityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Configuration("Manages beer stock")
public interface BeerControllerDocs {

    @Operation(description = "Beer creation operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Beer successfully saved"),
            @ApiResponse(responseCode = "400", description = "Missing required fields or wrong field range value")
    })
    ResponseEntity<BeerDto> save(@RequestBody @Valid BeerDto dto);

    @Operation(description = "Returns beer found by a given name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success beer found in the system"),
            @ApiResponse(responseCode = "404", description = "Beer with given name not found")
    })
    ResponseEntity<BeerDto> findByName(@PathVariable String name);

    @Operation(description = "Returns a list of all beers registered in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all beers registered in the system"),
    })
    ResponseEntity<List<BeerDto>> findAll();

    @Operation(description = "Delete a beer found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success beer deleted in the system"),
            @ApiResponse(responseCode = "404", description = "Beer with given id not found")
    })
    ResponseEntity<Void> delete(@PathVariable Long id);

    @Operation(description = "Beer full update/replace operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Beer successfully saved"),
            @ApiResponse(responseCode = "400", description = "Missing required fields or wrong field range value"),
            @ApiResponse(responseCode = "404", description = "Beer with given id not found")
    })
    ResponseEntity<Void> fullUpdate(@PathVariable Long id, @RequestBody @Valid BeerDto dto);

    @Operation(description = "Beer quantity increment operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Beer successfully saved"),
            @ApiResponse(responseCode = "400", description = "Missing required fields or wrong field range value"),
            @ApiResponse(responseCode = "422", description = "Quantity max limit exceeded because of the increment")
    })
    ResponseEntity<BeerDto> increment(@PathVariable Long id, @RequestBody @Valid QuantityDto dto);


    @Operation(description = "Beer quantity decrement operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Beer successfully saved"),
            @ApiResponse(responseCode = "400", description = "Missing required fields or wrong field range value"),
            @ApiResponse(responseCode = "422", description = "Negative quantity reached because of the decrement")
    })
    ResponseEntity<BeerDto> decrement(@PathVariable Long id, @RequestBody @Valid QuantityDto dto);
}
