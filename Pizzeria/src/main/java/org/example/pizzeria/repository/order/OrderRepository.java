package org.example.pizzeria.repository.order;

import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.StatusOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserApp_Id(Long userAppId);
    List<Order> findAllByStatusOrder(StatusOrder statusOrder);
    List<Order> findAllByOrderDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findAllByOrderDateTimeBetweenAndStatusOrder(LocalDateTime startDate, LocalDateTime endDate, StatusOrder statusOrder);
}
