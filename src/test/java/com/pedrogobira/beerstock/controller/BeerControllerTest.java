package com.pedrogobira.beerstock.controller;

import com.pedrogobira.beerstock.builder.BeerDtoBuilder;
import com.pedrogobira.beerstock.dto.BeerDto;
import com.pedrogobira.beerstock.dto.QuantityDto;
import com.pedrogobira.beerstock.exception.BeerStockExceededException;
import com.pedrogobira.beerstock.exception.NegativeStockException;
import com.pedrogobira.beerstock.exception.NotFoundException;
import com.pedrogobira.beerstock.service.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static com.pedrogobira.beerstock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {

    private static final String BEER_API_URL_PATH = "/api/v1/beers";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2L;
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BeerService service;

    @InjectMocks
    private BeerController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenCreatedStatusIsReturned() throws Exception {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();

        // When
        when(service.save(dto)).thenReturn(dto);

        // Then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.brand", is(dto.getBrand())))
                .andExpect(jsonPath("$.type", is(dto.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenBadRequestStatusIsReturned() throws Exception {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();
        dto.setName(null);

        // Then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();

        // When
        when(service.findByName(dto.getName())).thenReturn(dto);

        // Then
        mockMvc.perform(get(BEER_API_URL_PATH + "/" + dto.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.brand", is(dto.getBrand())))
                .andExpect(jsonPath("$.type", is(dto.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithInvalidNameThenNotFoundStatusIsReturned() throws Exception {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();

        // When
        when(service.findByName(dto.getName())).thenThrow(NotFoundException.class);

        // Then
        mockMvc.perform(get(BEER_API_URL_PATH + "/" + dto.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException));
    }

    @Test
    void whenGETAllIsCalledThenOkStatusIsReturned() throws Exception {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();

        // When
        when(service.findAll()).thenReturn(Collections.singletonList(dto));

        // Then
        mockMvc.perform(get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(dto.getName())))
                .andExpect(jsonPath("$[0].brand", is(dto.getBrand())))
                .andExpect(jsonPath("$[0].type", is(dto.getType().toString())));
    }

    @Test
    void whenGETAllIsCalledAndDatabaseIsEmptyThenOkStatusIsReturned() throws Exception {
        // When
        when(service.findAll()).thenReturn(Collections.emptyList());

        // Then
        mockMvc.perform(get(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledThenNoContentStatusIsReturned() throws Exception {
        // Given
        BeerDto dto = BeerDtoBuilder.builder().build().toBeerDto();

        // When
        doNothing().when(service).delete(dto.getId());

        // Then
        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + dto.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledAndIdIsInvalidThenNotFoundStatusIsReturned() throws Exception {
        // When
        doThrow(NotFoundException.class).when(service).delete(INVALID_BEER_ID);

        // Then
        mockMvc.perform(delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementAndQuantityIsValidThenOkStatusIsReturned() throws Exception {
        // Given
        BeerDto beerDto = BeerDtoBuilder.builder().build().toBeerDto();
        QuantityDto quantityDto = new QuantityDto(10);
        beerDto.setQuantity(beerDto.getQuantity() + quantityDto.getQuantity());

        // When
        when(service.increment(VALID_BEER_ID, quantityDto)).thenReturn(beerDto);

        // Then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDto.getName())))
                .andExpect(jsonPath("$.brand", is(beerDto.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDto.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDto.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementAndQuantityIsInvalidThenUnprocessableEntityStatusIsReturned() throws Exception {
        // Given
        BeerDto beerDto = BeerDtoBuilder.builder().build().toBeerDto();
        QuantityDto quantityDto = new QuantityDto(100);
        beerDto.setQuantity(beerDto.getQuantity() + quantityDto.getQuantity());

        // When
        when(service.increment(VALID_BEER_ID, quantityDto)).thenThrow(BeerStockExceededException.class);

        // Then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDto)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void whenPATCHIsCalledToDecrementAndQuantityIsValidThenOkStatusIsReturned() throws Exception {
        // Given
        BeerDto beerDto = BeerDtoBuilder.builder().build().toBeerDto();
        QuantityDto quantityDto = new QuantityDto(10);
        beerDto.setQuantity(beerDto.getQuantity() - quantityDto.getQuantity());

        // When
        when(service.decrement(VALID_BEER_ID, quantityDto)).thenReturn(beerDto);

        // Then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDto.getName())))
                .andExpect(jsonPath("$.brand", is(beerDto.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDto.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDto.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDecrementAndQuantityIsInvalidThenUnprocessableEntityStatusIsReturned() throws Exception {
        // Given
        BeerDto beerDto = BeerDtoBuilder.builder().build().toBeerDto();
        QuantityDto quantityDto = new QuantityDto(50);
        beerDto.setQuantity(beerDto.getQuantity() - quantityDto.getQuantity());

        // When
        when(service.decrement(VALID_BEER_ID, quantityDto)).thenThrow(NegativeStockException.class);

        // Then
        mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDto)))
                .andExpect(status().isUnprocessableEntity());
    }

}
