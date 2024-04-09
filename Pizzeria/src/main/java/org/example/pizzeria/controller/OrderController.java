package org.example.pizzeria.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.example.pizzeria.dto.order.*;
import org.example.pizzeria.dto.product.pizza.PizzaToBasketRequestDto;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.service.order.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(
        name = "Order controller",
        description = "All methods for working with basket and order"
)
@Validated
@RestController
@RequestMapping(path = "order",
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
    private final OrderServiceImpl orderService;

    @Autowired
    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Adding pizza to basket")
    @PostMapping("/addPizzaToBasket/{userId}")
    public ResponseEntity<BasketResponseDto> addPizzaToBasket(@Parameter(description = "user ID")
                                                                  @PathVariable @Positive Long userId,
                                                              @RequestBody @Valid PizzaToBasketRequestDto pizzaToBasketRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addPizzaToBasket(userId, pizzaToBasketRequestDto));
    }

    @Operation(summary = "Getting information about users basket")
    @GetMapping("/getBasketByUser/{userId}")
    public ResponseEntity<BasketResponseDto> getBasketByUser(@Parameter(description = "user ID")
                                                             @PathVariable @Positive Long userId) {
        return ResponseEntity.ok(orderService.getBasketByUser(userId));
    }

    @Operation(summary = "Changing count of pizza in basket")
    @PatchMapping("/changePizzasInBasket")
    public ResponseEntity<BasketResponseDto> changePizzasInBasket(@RequestBody @Valid BasketRequestDto requestDto) {
        return ResponseEntity.ok(orderService.changePizzasInBasket(requestDto));
    }

    @Operation(summary = "Moving details from basket to order")
    @PostMapping("/moveDetailsBasketToOrder/{id}")
    public ResponseEntity<OrderResponseDto> moveDetailsBasketToOrder(@Parameter(description = "basket ID")
                                                                     @PathVariable @Positive Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.moveDetailsBasketToOrder(id));
    }

    @Operation(summary = "Updating information about order ")
    @PatchMapping("/updateOrderAndOrderDetails/{id}")
    public ResponseEntity<OrderResponseDto> updateOrderAndOrderDetails(@Parameter(description = "order ID")
                                                                       @PathVariable @Positive Long id,
                                                                       @RequestBody @Valid OrderRequestDto orderRequestDto) {
        return ResponseEntity.ok(orderService.updateOrderAndOrderDetails(id, orderRequestDto));
    }

    @Operation(summary = "Deleting order")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@Parameter(description = "order ID")
                                              @PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok("Order deleted successfully");
    }

    @Operation(summary = "Updating status of order")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderStatusResponseDto> updateOrderStatus(@Parameter(description = "order ID")
                                                                    @PathVariable Long id,
                                                                    @Parameter(description = "Status order: NEW, PAID, CANCELED")
                                                                    @RequestParam StatusOrder status) {
        return ResponseEntity.ok(orderService.updateStatusOrder(id, status));
    }


    @Operation(summary = "Getting list all orders by user")
    @GetMapping("/getAllByUser/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getAllOrdersByUser(@Parameter(description = "user ID")
                                                                     @PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getAllOrdersByUser(userId));
    }

    @Operation(summary = "Getting information about order")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@Parameter(description = "order ID") @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderByUser(orderId));
    }

    @Operation(summary = "Getting list of orders by status")
    @GetMapping("/status")
    public ResponseEntity<List<OrderStatusResponseDto>> getOrdersByStatus(@Parameter(description = "Status order: NEW, PAID, CANCELED")
                                                                          @RequestParam StatusOrder status) {
        return ResponseEntity.ok(orderService.getOrderByStatus(status));
    }

    @Operation(summary = "Getting list of orders by period")
    @GetMapping("/period")
    public ResponseEntity<List<OrderStatusResponseDto>> getOrdersByPeriod(
            @Parameter(description = "Start date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(orderService.getAllOrdersByPeriod(startDate, endDate));
    }
}
