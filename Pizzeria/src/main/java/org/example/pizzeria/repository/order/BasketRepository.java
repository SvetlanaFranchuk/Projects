package org.example.pizzeria.repository.order;

import org.example.pizzeria.entity.order.Basket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepository extends JpaRepository<Basket, Long> {
    Optional<Basket> findByUserApp_Id(Long userId);

}
