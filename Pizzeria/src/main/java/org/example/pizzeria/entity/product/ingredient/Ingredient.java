package org.example.pizzeria.entity.product.ingredient;

import jakarta.persistence.*;
import lombok.*;
import org.example.pizzeria.entity.product.pizza.Pizza;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"pizzaSet"})
@EqualsAndHashCode(exclude = {"pizzaSet"})
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

    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "pizzas_ingredients",
            joinColumns = @JoinColumn(name = "ingredient_id"),
            inverseJoinColumns = @JoinColumn(name = "pizza_id"))
    private Set<Pizza> pizzaSet = new HashSet<>();
}
