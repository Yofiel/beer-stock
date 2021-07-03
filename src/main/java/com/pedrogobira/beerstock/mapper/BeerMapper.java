package com.pedrogobira.beerstock.mapper;

import com.pedrogobira.beerstock.dto.BeerDto;
import com.pedrogobira.beerstock.entity.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    BeerDto toDto(Beer beer);

    Beer toEntity(BeerDto dto);
}
