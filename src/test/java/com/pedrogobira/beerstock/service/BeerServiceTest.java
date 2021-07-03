package com.pedrogobira.beerstock.service;

import com.pedrogobira.beerstock.builder.BeerDtoBuilder;
import com.pedrogobira.beerstock.dto.BeerDto;
import com.pedrogobira.beerstock.dto.QuantityDto;
import com.pedrogobira.beerstock.entity.Beer;
import com.pedrogobira.beerstock.exception.BeerAlreadyExistsException;
import com.pedrogobira.beerstock.exception.BeerStockExceededException;
import com.pedrogobira.beerstock.exception.NegativeStockException;
import com.pedrogobira.beerstock.exception.NotFoundException;
import com.pedrogobira.beerstock.mapper.BeerMapper;
import com.pedrogobira.beerstock.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    @Mock
    private BeerRepository repository;

    private BeerMapper mapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService service;

    @Test
    void whenSaveIsCalledAndABeerIsGivenThenItShouldBeSaved() {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();
        Beer expectedBeer = mapper.toEntity(dto);

        // When
        when(repository.findByName(dto.getName())).thenReturn(Optional.empty());
        when(repository.save(expectedBeer)).thenReturn(expectedBeer);

        // Then
        BeerDto savedBeerDto = service.save(dto);
        assertThat(savedBeerDto.getId(), is(equalTo(expectedBeer.getId())));
        assertThat(savedBeerDto.getName(), is(equalTo(expectedBeer.getName())));
    }

    @Test
    void whenSaveIsCalledAndAnAlreadyRegisteredBeerIsGivenThenAnExceptionShouldBeThrown() {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();
        Beer expectedBeer = mapper.toEntity(dto);

        // When
        when(repository.findByName(dto.getName())).thenReturn(Optional.of(expectedBeer));

        // Then
        assertThrows(BeerAlreadyExistsException.class, () -> service.save(dto));
    }

    @Test
    void whenFindByNameIsCalledAndAValidBeerNameIsGivenThenReturnABeer() {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();
        Beer expectedBeer = mapper.toEntity(dto);

        // When
        when(repository.findByName(dto.getName())).thenReturn(Optional.of(expectedBeer));

        // Then
        BeerDto foundBeer = service.findByName(expectedBeer.getName());
        assertThat(dto, is(equalTo(foundBeer)));
    }

    @Test
    void whenFindByNameIsCalledAndAnInvalidBeerNameIsGivenThenAnExceptionShouldBeThrown() {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();

        // When
        when(repository.findByName(dto.getName())).thenReturn(Optional.empty());

        // Then
        assertThrows(NotFoundException.class, () -> service.findByName(dto.getName()));
    }

    @Test
    void whenFindAllIsCalledThenReturnAListOfBeers() {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();
        Beer expectedBeer = mapper.toEntity(dto);

        // When
        when(repository.findAll()).thenReturn(Collections.singletonList(expectedBeer));

        // Then
        List<BeerDto> returnedList = service.findAll();
        assertThat(returnedList, is(not(empty())));
        assertThat(returnedList.get(0), is(equalTo(dto)));
    }

    @Test
    void whenFindAllIsCalledAndDatabaseIsEmptyThenReturnAnEmptyList() {
        // When
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // Then
        List<BeerDto> returnedList = service.findAll();
        assertTrue(returnedList.isEmpty());
    }

    @Test
    void whenDeleteIsCalledAndAValidIdIsGivenThenDeleteBeer() {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();
        Beer expectedBeer = mapper.toEntity(dto);

        // When
        when(repository.findById(dto.getId())).thenReturn(Optional.of(expectedBeer));
        doNothing().when(repository).delete(expectedBeer);

        // Then
        service.delete(dto.getId());
        verify(repository, times(1)).findById(dto.getId());
        verify(repository, times(1)).delete(expectedBeer);
    }

    @Test
    void whenDeleteIsCalledAndAnInvalidIdIsGivenThenAnExceptionShouldBeThrown() {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();

        // When
        when(repository.findById(dto.getId())).thenReturn(Optional.empty());

        // Then
        assertThrows(NotFoundException.class, () -> service.delete(dto.getId()));
    }

    @Test
    void whenIncrementIsCalledAndAValidQuantityIsGivenThenIncrementBeerStock() {
        // Given
        BeerDto beerDto = BeerDtoBuilder.builder().build().toBeerDto();
        Beer expectedBeer = mapper.toEntity(beerDto);
        QuantityDto quantityDto = new QuantityDto(10);
        int expectedQuantity = expectedBeer.getQuantity() + quantityDto.getQuantity();

        // When
        when(repository.findById(beerDto.getId())).thenReturn(Optional.of(expectedBeer));
        when(repository.save(expectedBeer)).thenReturn(expectedBeer);

        // Then
        service.increment(beerDto.getId(), quantityDto);

        assertThat(expectedQuantity, equalTo(expectedBeer.getQuantity()));
        assertThat(expectedQuantity, lessThan(beerDto.getMax()));
    }

    @Test
    void whenIncrementIsCalledAndAnInvalidQuantityIsGivenThenAnExceptionShouldBeThrown() {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();
        Beer expectedBeer = mapper.toEntity(dto);
        QuantityDto quantityDto = new QuantityDto(50);

        // When
        when(repository.findById(dto.getId())).thenReturn(Optional.of(expectedBeer));

        // Then
        assertThrows(BeerStockExceededException.class, () -> service.increment(dto.getId(), quantityDto));
    }

    @Test
    void whenDecrementIsCalledAndAValidQuantityIsGivenThenDecrementBeerStock() {
        // Given
        BeerDto beerDto = BeerDtoBuilder.builder().build().toBeerDto();
        Beer expectedBeer = mapper.toEntity(beerDto);
        QuantityDto quantityDto = new QuantityDto(10);
        int expectedQuantity = beerDto.getQuantity() - quantityDto.getQuantity();

        // When
        when(repository.findById(beerDto.getId())).thenReturn(Optional.of(expectedBeer));
        when(repository.save(expectedBeer)).thenReturn(expectedBeer);

        // Then
        service.decrement(beerDto.getId(), quantityDto);

        assertThat(expectedQuantity, equalTo(expectedBeer.getQuantity()));
        assertThat(expectedQuantity, greaterThanOrEqualTo(0));
    }

    @Test
    void whenDecrementIsCalledAndAnInvalidQuantityIsGivenThenAnExceptionShouldBeThrown() {
        // Given
        BeerDto beerDto = BeerDtoBuilder.builder().build().toBeerDto();
        Beer expectedBeer = mapper.toEntity(beerDto);
        QuantityDto quantityDto = new QuantityDto(80);

        // When
        when(repository.findById(beerDto.getId())).thenReturn(Optional.of(expectedBeer));

        // Then
        assertThrows(NegativeStockException.class, () -> service.decrement(beerDto.getId(), quantityDto));
    }

}
