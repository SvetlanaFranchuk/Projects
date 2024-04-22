package org.example.pizzeria.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.pizzeria.entity.user.UserApp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orderDetailsSet", "userApp"})
@EqualsAndHashCode(exclude = {"orderDetailsSet", "userApp"})
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @UpdateTimestamp
    @Column(name = "order_date_time")
    private LocalDateTime orderDateTime;

    @Column(name = "delivery_date_time")
    private LocalDateTime deliveryDateTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_bonus")
    private TypeBonus typeBonus;

    @Column(name = "sum")
    private double sum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_order")
    private StatusOrder statusOrder;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetails> orderDetailsSet = new HashSet<>();

    @ManyToOne
    private UserApp userApp;

    public void addOrderDetails(OrderDetails orderDetails) {
        orderDetailsSet.add(orderDetails);
        orderDetails.setOrder(this);
    }

    public void removeOrderDetails(OrderDetails orderDetails) {
        orderDetailsSet.remove(orderDetails);
        orderDetails.setOrder(null);
    }

}
