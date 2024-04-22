package org.example.pizzeria.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userApp", "pizzas"})
@EqualsAndHashCode(exclude = {"userApp", "pizzas"})
@Entity
@Table(name = "baskets")
public class Basket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    private UserApp userApp;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "basket_pizza",
            joinColumns = @JoinColumn(name = "basket_id"),
            inverseJoinColumns = @JoinColumn(name = "pizza_id"))
    private List<Pizza> pizzas = new ArrayList<>();

    public void clear() {
        pizzas.clear();
    }

}
