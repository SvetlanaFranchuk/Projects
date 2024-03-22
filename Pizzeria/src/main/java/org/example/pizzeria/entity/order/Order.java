package org.example.pizzeria.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.pizzeria.entity.benefits.Review;
import org.example.pizzeria.entity.user.UserApp;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"orderDetail"})
@EqualsAndHashCode(exclude = {"orderDetail"})
@Builder
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp
    @Column(name = "order_date_time")
    private LocalDateTime orderDateTime;

    @Column (name = "sum")
    private double sum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_order")
    private StatusOrder statusOrder;

    @Embedded
    private DeliveryAddress deliveryAddress;

    @OneToOne(cascade = CascadeType.REMOVE)
    private OrderDetails orderDetail;

    @ManyToOne
    private UserApp userApp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order)  o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
