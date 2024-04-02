package org.example.pizzeria.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.example.pizzeria.dto.order.*;
import org.example.pizzeria.entity.order.StatusOrder;
import org.example.pizzeria.exception.EntityInPizzeriaNotFoundException;
import org.example.pizzeria.exception.ErrorMessage;
import org.example.pizzeria.exception.NotCorrectArgumentException;
import org.example.pizzeria.exception.order.InvalidOrderStatusException;
import org.example.pizzeria.service.order.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/addPizzaToBasket/{userId}/{pizzaId}/{countPizza}")
    public ResponseEntity<BasketResponseDto> addPizzaToBasket(@Parameter(description = "user ID") @PathVariable @Positive Long userId,
                                                              @Parameter(description = "pizza ID") @PathVariable @Positive Long pizzaId,
                                                              @Parameter(description = "count of pizzas") @PathVariable @Positive int countPizza) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addPizzaToBasket(userId, pizzaId, countPizza));
    }

    @Operation(summary = "Getting information about users basket")
    @GetMapping("/getBasketByUser/{userId}")
    public ResponseEntity<BasketResponseDto> getBasketByUser(@Parameter(description = "user ID")
                                                             @PathVariable @Positive Long userId) {
        BasketResponseDto basketResponseDto = orderService.getBasketByUser(userId);
        if (basketResponseDto != null)
            return ResponseEntity.ok(basketResponseDto);
        else
            return ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Changing count of pizza in basket")
    @PatchMapping("/changePizzasInBasket")
    public ResponseEntity<BasketResponseDto> changePizzasInBasket(@RequestBody @Valid BasketRequestDto requestDto) {
        BasketResponseDto basketResponseDto = orderService.changePizzasInBasket(requestDto);
        if (basketResponseDto != null)
            return ResponseEntity.ok(basketResponseDto);
        else
            return ResponseEntity.badRequest().build();
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
        try {
            OrderResponseDto responseDto = orderService.updateOrderAndOrderDetails(id, orderRequestDto);
            return ResponseEntity.ok(responseDto);
        } catch (EntityInPizzeriaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Deleting order")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@Parameter(description = "order ID")
                                              @PathVariable Long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.ok("Order deleted successfully");
        } catch (EntityInPizzeriaNotFoundException e) {
            return ResponseEntity.badRequest().body("Order" + ErrorMessage.ENTITY_NOT_FOUND);
        } catch (InvalidOrderStatusException e) {
            return ResponseEntity.badRequest().body(ErrorMessage.INVALID_STATUS_ORDER_FOR_DELETE);
        }
    }

    @Operation(summary = "Updating status of order")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderStatusResponseDto> updateOrderStatus(@Parameter(description = "order ID")
                                                                    @PathVariable Long id,
                                                                    @Parameter(description = "Status order: NEW, PAID, CANCELED")
                                                                    @RequestParam StatusOrder status) {
        try {
            OrderStatusResponseDto updatedOrderStatus = orderService.updateStatusOrder(id, status);
            return ResponseEntity.ok(updatedOrderStatus);
        } catch (EntityInPizzeriaNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Getting list all orders by user")
    @GetMapping("/getAllByUser/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getAllOrdersByUser(@Parameter(description = "user ID")
                                                                     @PathVariable Long userId) {
        List<OrderResponseDto> orderResponseDtoList = orderService.getAllOrdersByUser(userId);
        return ResponseEntity.ok(orderResponseDtoList);
    }

    @Operation(summary = "Getting information about order")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@Parameter(description = "order ID") @PathVariable Long orderId) {
        OrderResponseDto orderResponseDto = orderService.getOrderByUser(orderId);
        return ResponseEntity.ok(orderResponseDto);
    }

    @Operation(summary = "Getting list of orders by status")
    @GetMapping("/status")
    public ResponseEntity<List<OrderStatusResponseDto>> getOrdersByStatus(@Parameter(description = "Status order: NEW, PAID, CANCELED")
                                                                          @RequestParam StatusOrder status) {
        List<OrderStatusResponseDto> orderStatusResponseDtoList = orderService.getOrderByStatus(status);
        if (orderStatusResponseDtoList.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(orderStatusResponseDtoList);
        }
    }

    @Operation(summary = "Getting list of orders by period")
    @GetMapping("/period")
    public ResponseEntity<List<OrderStatusResponseDto>> getOrdersByPeriod(
            @Parameter(description = "Start date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (startDate.isAfter(endDate)) {
            throw new NotCorrectArgumentException(ErrorMessage.NOT_CORRECT_ARGUMENT);
        }

        List<OrderStatusResponseDto> orderStatusResponseDtoList = orderService.getAllOrdersByPeriod(startDate, endDate);
        return ResponseEntity.ok(orderStatusResponseDtoList);
    }
}
