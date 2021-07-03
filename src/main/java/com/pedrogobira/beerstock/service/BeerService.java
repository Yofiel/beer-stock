package com.pedrogobira.beerstock.service;

import com.pedrogobira.beerstock.dto.BeerDto;
import com.pedrogobira.beerstock.dto.QuantityDto;
import com.pedrogobira.beerstock.entity.Beer;
import com.pedrogobira.beerstock.exception.BeerAlreadyExistsException;
import com.pedrogobira.beerstock.exception.BeerStockExceededException;
import com.pedrogobira.beerstock.exception.NegativeStockException;
import com.pedrogobira.beerstock.exception.NotFoundException;
import com.pedrogobira.beerstock.mapper.BeerMapper;
import com.pedrogobira.beerstock.repository.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

    private final BeerRepository repository;
    private final BeerMapper mapper = BeerMapper.INSTANCE;

    @Transactional
    public BeerDto save(BeerDto dto) {
        verifyIfBeerIsAlreadyRegistered(dto.getName());
        Beer beer = mapper.toEntity(dto);
        beer = repository.save(beer);
        return mapper.toDto(beer);
    }

    @Transactional(readOnly = true)
    public BeerDto findByName(String name) {
        return mapper.toDto(verifyByNameIfBeerExists(name));
    }

    @Transactional(readOnly = true)
    public List<BeerDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(verifyByIdIfBeerExists(id));
    }

    @Transactional
    public void update(Long id, BeerDto dto) {
        verifyByIdIfBeerExists(id);
        dto.setId(id);
        Beer beer = mapper.toEntity(dto);
        repository.save(beer);
    }

    @Transactional
    public BeerDto increment(Long id, QuantityDto dto) {
        Beer beer = verifyByIdIfBeerExists(id);
        int newQuantity = beer.getQuantity() + dto.getQuantity();
        if (newQuantity <= beer.getMax()) {
            return mapper.toDto(setNewStockQuantity(beer, newQuantity));
        } else {
            throw new BeerStockExceededException();
        }
    }

    @Transactional
    public BeerDto decrement(Long id, QuantityDto dto) {
        Beer beer = verifyByIdIfBeerExists(id);
        int newQuantity = beer.getQuantity() - dto.getQuantity();
        if (newQuantity >= 0) {
            return mapper.toDto(setNewStockQuantity(beer, newQuantity));
        } else {
            throw new NegativeStockException();
        }
    }

    @Transactional
    private Beer setNewStockQuantity(Beer beer, int newQuantity) {
        beer.setQuantity(newQuantity);
        return repository.save(beer);
    }

    private void verifyIfBeerIsAlreadyRegistered(String name) {
        Optional<Beer> optionalBeer = repository.findByName(name);
        if (optionalBeer.isPresent()) throw new BeerAlreadyExistsException(name);
    }

    private Beer verifyByNameIfBeerExists(String name) {
        return repository.findByName(name).orElseThrow(NotFoundException::new);
    }

    private Beer verifyByIdIfBeerExists(Long id) {
        return repository.findById(id).orElseThrow(NotFoundException::new);
    }

}
