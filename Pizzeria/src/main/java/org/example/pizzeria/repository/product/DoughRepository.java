package org.example.pizzeria.repository.product;

import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.TypeDough;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoughRepository extends JpaRepository<Dough, Integer> {
    List<Dough> findAllByTypeDoughAndSmallPrice(TypeDough typeDough, double smallPrice);
}
