package org.example.pizzeria.repository.product;

import org.example.pizzeria.entity.product.ingredient.Dough;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoughRepository extends JpaRepository<Dough, Integer> {

}
