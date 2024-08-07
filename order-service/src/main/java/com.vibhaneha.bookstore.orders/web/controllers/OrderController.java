package com.vibhaneha.bookstore.orders.web.controllers;

import com.vibhaneha.bookstore.orders.domain.OrderNotFoundException;
import com.vibhaneha.bookstore.orders.domain.OrderService;
import com.vibhaneha.bookstore.orders.domain.SecurityService;
import com.vibhaneha.bookstore.orders.domain.models.CreateOrderRequest;
import com.vibhaneha.bookstore.orders.domain.models.CreateOrderResponse;
import com.vibhaneha.bookstore.orders.domain.models.OrderDTO;
import com.vibhaneha.bookstore.orders.domain.models.OrderSummary;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    private final SecurityService securityService;

    public OrderController(OrderService orderService, SecurityService securityService) {
        this.orderService = orderService;
        this.securityService = securityService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String userName = securityService.getLoginUserName();
        log.info("Creating order for the user: {} " + userName);
        return orderService.createOrder(userName, request);
    }

    @GetMapping
    List<OrderSummary> getOrders() {
        String userName = securityService.getLoginUserName();
        log.info("Fetching orders for the user: {} " + userName);
        return orderService.findOrders(userName);
    }

    @GetMapping(value = "/{orderNumber}")
    OrderDTO getOrder(@PathVariable(value = "orderNumber") String orderNumber) {
        log.info("Fetching the order by id: {} " + orderNumber);
        String userName = securityService.getLoginUserName();
        return orderService
                .findUserOrder(userName, orderNumber)
                .orElseThrow(() -> new OrderNotFoundException(orderNumber));
    }

    /* @GetMapping(value = "/{orderNumber}")
    Optional<OrderDTO> getOrder(@PathVariable(value = "orderNumber") String orderNumber) {
        log.info("Fetching order by id: {}", orderNumber);
        String userName = securityService.getLoginUserName();
        try {
            log.info("Before fetching");
            final Optional<OrderDTO> userOrder = orderService
                    .findUserOrder(userName, orderNumber);
            log.info("userOrder {} "+userOrder.get().getTotalAmount());
            return userOrder;
        }
        catch(OrderNotFoundException exception){
            throw  new OrderNotFoundException(orderNumber);
        }
       // return null;
    }*/
}
