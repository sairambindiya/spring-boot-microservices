package com.vibhaneha.bookstore.orders.domain;

import com.vibhaneha.bookstore.orders.domain.models.CreateOrderRequest;
import com.vibhaneha.bookstore.orders.domain.models.OrderDTO;
import com.vibhaneha.bookstore.orders.domain.models.OrderItem;
import com.vibhaneha.bookstore.orders.domain.models.OrderStatus;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderMapper {
    private static final Logger log = LoggerFactory.getLogger(OrderMapper.class);

    static OrderEntity covertToEntity(CreateOrderRequest request) {
        OrderEntity newOrder = new OrderEntity();
        newOrder.setOrderNumber(UUID.randomUUID().toString());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setCustomer(request.customer());
        newOrder.setDeliveryAddress(request.deliveryAddress());
        Set<OrderItemEntity> orderItems = new HashSet<>();
        for (OrderItem orderItemEntity : request.items()) {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setCode(orderItemEntity.code());
            orderItem.setOrder(newOrder);
            orderItem.setCode(orderItemEntity.code());
            orderItem.setName(orderItemEntity.name());
            orderItem.setPrice(orderItemEntity.price());
            orderItem.setQuantity(orderItemEntity.quantity());
            orderItems.add(orderItem);
        }
        newOrder.setItems(orderItems);
        return newOrder;
    }

    static OrderDTO convertToDTO(OrderEntity order) {

        Set<OrderItem> orderItems = new HashSet<>();

        if (order.getItems() != null) {
            for (OrderItemEntity item : order.getItems()) {
                OrderItem newItem = new OrderItem(item.getCode(), item.getName(), item.getPrice(), item.getQuantity());
                orderItems.add(newItem);
            }
        } else {
            log.warn("OrderEntity with orderNumber: {} has null items set", order.getOrderNumber());
        }
        /* //final Set<OrderItem> orderItems = order.getItems().stream().map(item -> new OrderItem(item.getCode(), item.getName(), item.getPrice(), item.getQuantity()))
               // .collect(Collectors.toSet());
        final Set<OrderItem> orderItems = new HashSet<>();
        for (OrderItemEntity item : order.getItems()) {
            OrderItem newItem = new OrderItem(item.getCode(), item.getName(), item.getPrice(), item.getQuantity());
            orderItems.add(newItem);
        }

        log.info("Fetching the order number from mapper class: {} and items size: {}", order.getOrderNumber(), orderItems.size());
        log.info("Fetching order number before converting to DTO {} and order items {} "+order.getOrderNumber() +orderItems);*/
        return new OrderDTO(
                order.getOrderNumber(),
                order.getUserName(),
                orderItems,
                order.getCustomer(),
                order.getDeliveryAddress(),
                order.getStatus(),
                order.getComments(),
                order.getCreatedAt());
    }
}
