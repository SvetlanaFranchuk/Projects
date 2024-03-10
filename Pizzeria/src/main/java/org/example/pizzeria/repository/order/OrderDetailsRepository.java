package org.example.pizzeria.repository.order;

import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.OrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Long> {
     Optional<OrderDetails> findByOrder(Order order);
     List<OrderDetails> findByDeliveryDateTimeBetween (LocalDateTime startDate, LocalDateTime endDate);

}
