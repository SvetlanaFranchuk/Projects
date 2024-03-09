package org.example.pizzeria.entity.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "baskets")
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    private UserApp userApp;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pizza> pizzas = new ArrayList<>();

    public void addPizza(Pizza pizza){
        pizzas.add(pizza);
    }

    public void removePizza(Pizza pizza){
        pizzas.remove(pizza);
    }
}
