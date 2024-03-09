package org.example.pizzeria.entity.product.pizza;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pizzeria.entity.product.ingredient.Dough;
import org.example.pizzeria.entity.product.ingredient.Ingredient;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pizzas")
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
    private boolean isStandardRecipe;

    @ManyToOne(fetch = FetchType.LAZY)
    private Dough dough;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "pizzaSet")
    private List<Ingredient> ingredientsList = new ArrayList<>();



}
