package org.example.pizzeria.mapper.product;

import org.example.pizzeria.dto.product.dough.DoughCreateRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface DoughMapper {

    Dough toDough(DoughCreateRequestDto newDough);
    Dough toDough(DoughResponseClientDto dough);

    DoughResponseDto toDoughResponseDto(Dough dough);

    DoughResponseClientDto toDoughResponseClientDto(Dough dough);

}
