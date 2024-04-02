package org.example.pizzeria.repository.order;

import org.example.pizzeria.entity.order.Order;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.entity.user.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserApp_Id(Long userAppId);
    List<Order> findAllByStatusOrder(StatusOrder statusOrder);
    List<Order> findAllByOrderDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
}
