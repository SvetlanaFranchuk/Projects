package org.example.pizzeria.mapper.product;

import org.example.pizzeria.dto.product.dough.DoughCreateRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DoughMapper {

    @Mapping(target = "id", ignore = true)
    Dough toDough(DoughCreateRequestDto newDough);

    @Mapping(target = "smallPrice", ignore = true)
    Dough toDough(DoughResponseClientDto dough);

    DoughResponseDto toDoughResponseDto(Dough dough);

    DoughResponseClientDto toDoughResponseClientDto(Dough dough);

}
