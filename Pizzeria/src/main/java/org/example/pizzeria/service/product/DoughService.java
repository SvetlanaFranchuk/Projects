package org.example.pizzeria.service.product;

import org.example.pizzeria.dto.product.dough.DoughCreateRequestDto;
import org.example.pizzeria.dto.product.dough.DoughResponseClientDto;
import org.example.pizzeria.dto.product.dough.DoughResponseDto;
import org.example.pizzeria.dto.product.dough.DoughUpdateRequestDto;

import java.util.List;

public interface DoughService {

    DoughResponseDto add(DoughCreateRequestDto newDough);

    DoughResponseDto update(DoughUpdateRequestDto dough, Integer id);

    void delete(Integer id);

    List<DoughResponseDto> getAllForAdmin();

    List<DoughResponseClientDto> getAllForClient();

}


