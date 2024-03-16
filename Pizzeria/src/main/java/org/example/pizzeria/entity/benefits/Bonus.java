package org.example.pizzeria.entity.benefits;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Bonus {

    @Column(name = "count_orders")
    private int countOrders;

    @Column(name = "sum_orders")
    private double sumOrders;

}
