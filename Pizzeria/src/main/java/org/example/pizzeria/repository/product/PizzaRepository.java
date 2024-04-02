package org.example.pizzeria.repository.product;

import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    List<Pizza> findAllByStandardRecipeAndStyles(boolean isStandardRecipe, Styles styles);
    List<Pizza> findAllByStandardRecipe(boolean isStandardRecipe);

    List<Pizza> findAllByStandardRecipeAndToppingsFillings(boolean isStandardRecipe, ToppingsFillings toppingsFillings);

    List<Pizza> findAllByStandardRecipeAndToppingsFillingsAndStyles(boolean isStandardRecipe, ToppingsFillings toppingsFillings, Styles styles);

    List<Pizza> findAllByDoughIs(Dough dough);

    List<Pizza> findAllByIngredientsListIsContaining(Ingredient ingredient);

}
