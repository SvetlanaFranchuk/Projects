package org.example.pizzeria.service.benefits;

import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;

import java.util.List;

public interface FavoritesService {

    FavoritesResponseDto addPizzaToUserFavorite(Long userId, Long pizzaId);

    void deletePizzaFromUserFavorite(Long pizzaId, Long userId);

    List<PizzaResponseDto> getAllFavoritePizzaByUser(Long userId);
}


