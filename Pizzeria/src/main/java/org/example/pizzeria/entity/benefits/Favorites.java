package org.example.pizzeria.entity.benefits;

import jakarta.persistence.*;
import lombok.*;
import org.example.pizzeria.entity.product.pizza.Pizza;
import org.example.pizzeria.entity.user.UserApp;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userApp", "pizzas"})
@EqualsAndHashCode(exclude = {"userApp", "pizzas"})

@Entity
@Table(name = "favorites")
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "pizzas_favorites")
    private List<Pizza> pizzas;

    @OneToOne(fetch = FetchType.LAZY)
    private UserApp userApp;
}
