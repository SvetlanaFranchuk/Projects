package org.example.pizzeria.entity.product.ingredient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pizzeria.entity.product.pizza.Pizza;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "weight")
    private int weight;

    @Column(name = "nutrition")
    private int nutrition;

    @Column(name = "price")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_ingredient")
    private GroupIngredient groupIngredient;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "pizzas_ingredients")
    private Set<Pizza> pizzaSet = new HashSet<>();
}
