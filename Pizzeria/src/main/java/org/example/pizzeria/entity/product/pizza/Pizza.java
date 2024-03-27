package org.example.pizzeria.entity.product.pizza;

import jakarta.persistence.*;
import lombok.*;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.Ingredient;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"dough", "ingredientsList"})
@EqualsAndHashCode(exclude = {"dough", "ingredientsList"})
@Builder
@Entity
@Table(name = "pizzas",
uniqueConstraints = {
@UniqueConstraint(columnNames = "title")
})
public class Pizza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "styles")
    private Styles styles;

    @Enumerated(EnumType.STRING)
    @Column(name = "toppings_fillings")
    private ToppingsFillings toppingsFillings;

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private TypeBySize size;

    @Column(name = "is_standard_recipe")
    private boolean standardRecipe;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "nutrition")
    private int nutrition;

    @ManyToOne(fetch = FetchType.LAZY)
    private Dough dough;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "pizzaSet")
    private List<Ingredient> ingredientsList = new ArrayList<>();



}
