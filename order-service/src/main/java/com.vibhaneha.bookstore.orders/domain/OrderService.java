package com.vibhaneha.bookstore.orders.domain;

import com.vibhaneha.bookstore.orders.domain.models.*;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private static final List<String> DELIVERY_ALLOWED_COUNTRIES = List.of("INDIA", "GERMANY", "UK", "USA");

    private final OrderRepository repository;

    private final OrderValidator orderValidator;

    private final OrderEventService orderEventService;

    public OrderService(
            OrderRepository repository, OrderValidator orderValidator, OrderEventService orderEventService) {
        this.repository = repository;
        this.orderValidator = orderValidator;
        this.orderEventService = orderEventService;
    }

    public CreateOrderResponse createOrder(String userName, CreateOrderRequest request) {
        orderValidator.validate(request);
        OrderEntity newOrder = OrderMapper.covertToEntity(request);
        newOrder.setUserName(userName);
        OrderEntity savedOrder = this.repository.save(newOrder);
        log.info("Created order with order number :" + newOrder.getOrderNumber());
        OrderCreatedEvent orderCreatedEvent = OrderEventMapper.buildOrderCreatedEvent(newOrder);
        orderEventService.save(orderCreatedEvent);
        return new CreateOrderResponse(savedOrder.getOrderNumber());
    }

    public void processNewOrders() {
        List<OrderEntity> orders = repository.findByStatus(OrderStatus.NEW);
        log.info("Found {} orders to process", orders.size());
        for (OrderEntity order : orders) {
            this.process(order);
        }
    }

    private void process(OrderEntity order) {
        try {
            if (canBeDelivered(order)) {
                log.info("Order Number: {} can be delivered", order.getOrderNumber());
                repository.updateOrderStatus(order.getOrderNumber(), OrderStatus.DELIVERED);
                orderEventService.save(OrderEventMapper.buildOrderDeliveredEvent(order));
            } else {
                log.info("Order Number {} can't be delivered" + order.getOrderNumber());
                repository.updateOrderStatus(order.getOrderNumber(), OrderStatus.CANCELLED);
                orderEventService.save(
                        OrderEventMapper.buildOrderCancelledEvent(order, "Order can't be delivered to this location"));
            }

        } catch (RuntimeException exception) {
            log.error("Failed to process order with order number: {}" + order.getOrderNumber(), exception);
            repository.updateOrderStatus(order.getOrderNumber(), OrderStatus.ERROR);
            orderEventService.save(OrderEventMapper.buildOrderErrorEvent(order, exception.getMessage()));
        }
    }

    private boolean canBeDelivered(OrderEntity order) {
        return DELIVERY_ALLOWED_COUNTRIES.contains(
                order.getDeliveryAddress().country().toUpperCase());
    }

    public List<OrderSummary> findOrders(String userName) {
        return repository.findByUserName(userName);
    }

    public Optional<OrderDTO> findUserOrder(String userName, String orderNumber) {
        log.info("Found the order " + userName + " ," + orderNumber);
        return repository.findByUserNameAndOrderNumber(userName, orderNumber).map(OrderMapper::convertToDTO);
    }
}
