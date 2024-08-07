package com.vibhaneha.bookstore.orders.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibhaneha.bookstore.orders.domain.models.OrderCancelledEvent;
import com.vibhaneha.bookstore.orders.domain.models.OrderCreatedEvent;
import com.vibhaneha.bookstore.orders.domain.models.OrderDeliveredEvent;
import com.vibhaneha.bookstore.orders.domain.models.OrderErrorEvent;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class OrderEventService {
    private static final Logger log = LoggerFactory.getLogger(OrderEventService.class);

    private final OrderEventRepository orderEventRepository;

    private final OrderEventPublisher orderEventPublisher;

    private final ObjectMapper objectMapper;

    public OrderEventService(
            OrderEventRepository orderEventRepository,
            OrderEventPublisher orderEventPublisher,
            ObjectMapper objectMapper) {
        this.orderEventRepository = orderEventRepository;
        this.orderEventPublisher = orderEventPublisher;
        this.objectMapper = objectMapper;
    }

    void save(OrderCreatedEvent event) {
        OrderEventEntity orderEvent = new OrderEventEntity();
        orderEvent.setEventId(event.eventId());
        orderEvent.setEventType(OrderEventType.ORDER_CREATED);
        orderEvent.setOrderNumber(event.orderNumber());
        orderEvent.setPayload(toJsonPayload(event));
        orderEvent.setCreatedAt(event.createdAt());
        this.orderEventRepository.save(orderEvent);
    }

    void save(OrderDeliveredEvent event) {
        OrderEventEntity orderEvent = new OrderEventEntity();
        orderEvent.setEventId(event.eventId());
        orderEvent.setEventType(OrderEventType.ORDER_DELIVERED);
        orderEvent.setOrderNumber(event.orderNumber());
        orderEvent.setPayload(toJsonPayload(event));
        orderEvent.setCreatedAt(event.createdAt());
        this.orderEventRepository.save(orderEvent);
    }

    void save(OrderCancelledEvent event) {
        OrderEventEntity orderEvent = new OrderEventEntity();
        orderEvent.setEventId(event.eventId());
        orderEvent.setEventType(OrderEventType.ORDER_CANCELLED);
        orderEvent.setOrderNumber(event.orderNumber());
        orderEvent.setPayload(toJsonPayload(event));
        orderEvent.setCreatedAt(event.createdAt());
        this.orderEventRepository.save(orderEvent);
    }

    void save(OrderErrorEvent event) {
        OrderEventEntity orderEvent = new OrderEventEntity();
        orderEvent.setEventId(event.eventId());
        orderEvent.setEventType(OrderEventType.ORDER_PROCESSING_FAILED);
        orderEvent.setOrderNumber(event.orderNumber());
        orderEvent.setPayload(toJsonPayload(event));
        orderEvent.setCreatedAt(event.createdAt());
        this.orderEventRepository.save(orderEvent);
    }

    private String toJsonPayload(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void publishOrderEvents() {
        log.info("Publish Order Events at {}", Instant.now());
        Sort sort = Sort.by("createdAt").ascending();
        List<OrderEventEntity> events = orderEventRepository.findAll(sort);
        log.info("Found {} Order Events to be published ", events.size());
        for (OrderEventEntity event : events) {
            log.info("Publishing Order Event: {}", event);
            this.publishEvent(event);
            orderEventRepository.delete(event);
        }
    }

    private void publishEvent(OrderEventEntity event) {
        OrderEventType orderEventType = event.getEventType();
        switch (orderEventType) {
            case ORDER_CREATED:
                OrderCreatedEvent orderCreatedEvent = fromJsonPayload(event.getPayload(), OrderCreatedEvent.class);
                orderEventPublisher.publish(orderCreatedEvent);
                break;
            case ORDER_DELIVERED:
                OrderDeliveredEvent orderDeliveredEvent =
                        fromJsonPayload(event.getPayload(), OrderDeliveredEvent.class);
                orderEventPublisher.publish(orderDeliveredEvent);
                break;
            case ORDER_CANCELLED:
                OrderCancelledEvent orderCancelledEvent =
                        fromJsonPayload(event.getPayload(), OrderCancelledEvent.class);
                orderEventPublisher.publish(orderCancelledEvent);
                break;
            case ORDER_PROCESSING_FAILED:
                OrderErrorEvent orderErrorEvent = fromJsonPayload(event.getPayload(), OrderErrorEvent.class);
                orderEventPublisher.publish(orderErrorEvent);
            default:
                log.warn("Unsupported Event Type: {} " + orderEventType);
        }
    }

    private <T> T fromJsonPayload(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
