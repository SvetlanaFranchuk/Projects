package org.example.pizzeria.entity.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    @OneToOne(cascade = CascadeType.REMOVE)
    private OrderDetails orderDetail;

    @ManyToOne
    private UserApp userApp;

}
