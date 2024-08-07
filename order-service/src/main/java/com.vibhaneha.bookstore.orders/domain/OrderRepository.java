package com.vibhaneha.bookstore.orders.domain;

import com.vibhaneha.bookstore.orders.domain.models.OrderStatus;
import com.vibhaneha.bookstore.orders.domain.models.OrderSummary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByStatus(OrderStatus orderStatus);

    Optional<Object> findByOrderNumber(String orderNumber);

    default void updateOrderStatus(String orderNumber, OrderStatus orderStatus) {
        OrderEntity order = (OrderEntity) this.findByOrderNumber(orderNumber).orElseThrow();
        order.setStatus(orderStatus);
        this.save(order);
    }

    @Query(
            """
        select new com.vibhaneha.bookstore.orders.domain.models.OrderSummary(o.orderNumber, o.status)
        from OrderEntity o
        where o.userName = :userName
        """)
    List<OrderSummary> findByUserName(String userName);

    @Query(
            """
        select distinct o
        from OrderEntity o left join fetch o.items
        where o.userName = :userName and o.orderNumber = :orderNumber
        """)
    Optional<OrderEntity> findByUserNameAndOrderNumber(String userName, String orderNumber);
}
