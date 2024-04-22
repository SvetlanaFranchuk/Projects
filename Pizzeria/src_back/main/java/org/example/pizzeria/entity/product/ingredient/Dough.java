package org.example.pizzeria.entity.product.ingredient;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
