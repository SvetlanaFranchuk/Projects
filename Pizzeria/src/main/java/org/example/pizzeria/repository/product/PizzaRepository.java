package org.example.pizzeria.repository.product;

import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.product.pizza.Styles;
import org.example.pizzeria.entity.product.pizza.ToppingsFillings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, Long> {
    List<Pizza> findAllByStyles(Styles styles);

    Optional<Pizza> findByTitle(String title);

    List<Pizza> findAllByToppingsFillings(ToppingsFillings toppingsFillings);

    List<Pizza> findAllByToppingsFillingsAndStyles(ToppingsFillings toppingsFillings, Styles styles);

}
