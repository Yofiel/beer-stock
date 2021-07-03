package com.pedrogobira.beerstock.builder;

import com.pedrogobira.beerstock.dto.BeerDto;
import com.pedrogobira.beerstock.enums.BeerType;
import lombok.Builder;

@Builder
public class BeerDtoBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Brahma";

    @Builder.Default
    private String brand = "Ambev";

    @Builder.Default
    private Integer max = 50;

    @Builder.Default
    private Integer quantity = 10;

    @Builder.Default
    private BeerType type = BeerType.LAGER;

    public BeerDto toBeerDto() {
        return new BeerDto(id, name, brand, max, quantity, type);
    }
}
