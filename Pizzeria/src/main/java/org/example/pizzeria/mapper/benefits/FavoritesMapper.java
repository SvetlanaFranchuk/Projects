package org.example.pizzeria.mapper.benefits;

import lombok.RequiredArgsConstructor;
import org.example.pizzeria.dto.benefits.FavoritesResponseDto;
import org.example.pizzeria.dto.product.pizza.PizzaResponseDto;
import org.example.pizzeria.entity.benefits.Favorites;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;
import org.example.pizzeria.mapper.product.DoughMapper;
import org.example.pizzeria.mapper.product.IngredientMapper;
import org.example.pizzeria.mapper.product.PizzaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class FavoritesMapper {

    private final PizzaMapper pizzaMapper;
    private final DoughMapper doughMapper;
    private final IngredientMapper ingredientMapper;

   @Autowired
   public FavoritesMapper(PizzaMapper pizzaMapper, DoughMapper doughMapper, IngredientMapper ingredientMapper) {
        this.pizzaMapper = pizzaMapper;
        this.doughMapper = doughMapper;
        this.ingredientMapper = ingredientMapper;
    }

    public Favorites ToFavorites(List<Pizza> pizzas, UserApp userApp) {
        Favorites favorites = new Favorites();
        favorites.setPizzas(pizzas);
        favorites.setUserApp(userApp);
        return favorites;
    }

    public FavoritesResponseDto toFavoriteResponseDto(Favorites favorites) {
        List<PizzaResponseDto> pizzaResponseDtos = favorites.getPizzas().stream()
                .map(pizzaMapper::toPizzaResponseDto).toList();
        return new FavoritesResponseDto(new ArrayList<>(pizzaResponseDtos));
    }


}
