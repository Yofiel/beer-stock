package com.pedrogobira.beerstock.controller;

import com.pedrogobira.beerstock.dto.BeerDto;
import com.pedrogobira.beerstock.dto.QuantityDto;
import com.pedrogobira.beerstock.service.BeerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/v1/beers")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerController implements BeerControllerDocs {

    private final BeerService service;

    @PostMapping
    public ResponseEntity<BeerDto> save(@RequestBody @Valid BeerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dto));
    }

    @GetMapping("/{name}")
    public ResponseEntity<BeerDto> findByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(service.findByName(name));
    }

    @GetMapping
    public ResponseEntity<List<BeerDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> fullUpdate(@PathVariable Long id, @RequestBody @Valid BeerDto dto) {
        service.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}/increment")
    public ResponseEntity<BeerDto> increment(@PathVariable Long id, @RequestBody @Valid QuantityDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(service.increment(id, dto));
    }

    @PatchMapping("/{id}/decrement")
    public ResponseEntity<BeerDto> decrement(@PathVariable Long id, @RequestBody @Valid QuantityDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(service.decrement(id, dto));
    }

}
