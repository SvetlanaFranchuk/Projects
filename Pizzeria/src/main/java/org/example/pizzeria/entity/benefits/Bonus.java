package org.example.pizzeria.entity.benefits;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pizzeria.entity.order.OrderDetails;
import org.example.pizzeria.entity.user.UserApp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bonuses")
public class Bonus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_bonuses")
    private TypeBonus typeBonus;

    @Column(name = "count_orders")
    private int countOrders;

    @Column(name = "sum_orders")
    private double sumOrders;

    @ManyToOne
    private UserApp userApp;

    @ManyToOne
    private OrderDetails orderDetails;
}
