package org.example.pizzeria.mapper.benefits;

import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.mapper.product.PizzaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FavoritesMapper {

    private final PizzaMapper pizzaMapper;

    @Autowired
    public FavoritesMapper(PizzaMapper pizzaMapper) {
        this.pizzaMapper = pizzaMapper;
    }

    public FavoritesResponseDto toFavoriteResponseDto(Favorites favorites) {
        List<PizzaResponseDto> pizzaResponseDtoList = favorites.getPizzas().stream()
                .map(pizzaMapper::toPizzaResponseDto).toList();
        return new FavoritesResponseDto(new ArrayList<>(pizzaResponseDtoList));
    }


}
