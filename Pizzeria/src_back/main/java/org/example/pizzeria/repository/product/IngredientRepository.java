package org.example.pizzeria.repository.product;

import org.example.pizzeria.entity.product.ingredient.GroupIngredient;
import org.example.pizzeria.entity.product.ingredient.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findAllByGroupIngredient(GroupIngredient groupIngredient);

    List<Ingredient> findAllByNameAndPrice(String name, double price);
}
