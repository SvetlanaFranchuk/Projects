package org.example.pizzeria.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.pizzeria.entity.product.pizza.Pizza;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"order", "pizza"})
@EqualsAndHashCode(exclude = {"order", "pizza"})
@Builder
@Entity
@Table(name = "order_details")
public class OrderDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pizza_id")
    private Pizza pizza;

    @Column(name = "quantity")
    private int quantity;

    @OneToOne
    private Order order;

}
