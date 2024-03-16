package org.example.pizzeria.entity.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pizzeria.entity.product.pizza.Pizza;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders_details")
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "delivery_date_time")
    private LocalDateTime deliveryDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_bonus")
    private TypeBonus typeBonus;

    @OneToOne
    private Order order;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pizza> pizzas = new ArrayList<>();

    public void addPizza(Pizza pizza){
        pizzas.add(pizza);
    }

    public void removePizza(Pizza pizza){
        pizzas.remove(pizza);
    }

}
