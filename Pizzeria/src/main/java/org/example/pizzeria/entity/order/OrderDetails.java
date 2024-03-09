package org.example.pizzeria.entity.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pizzeria.entity.benefits.Bonus;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToOne
    private Order order;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pizza> pizzas = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bonus> bonuses = new ArrayList<>();


    public void addPizza(Pizza pizza){
        pizzas.add(pizza);
    }

    public void removePizza(Pizza pizza){
        pizzas.remove(pizza);
    }

    public void addBonus(Bonus bonus){
        bonuses.add(bonus);
        bonus.setOrderDetails(this);
    }

    public void removeBonus(Bonus bonus){
        bonuses.remove(bonus);
        bonus.setOrderDetails(null);
    }
}
