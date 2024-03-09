package org.example.pizzeria.entity.product.ingredient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doughs")
public class Dough {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_dough")
    private TypeDough typeDough;

    @Column(name = "small_weight")
    private int smallWeight;

    @Column(name = "small_nutrition")
    private int smallNutrition;

    @Column(name = "small_price")
    private double smallPrice;

}
