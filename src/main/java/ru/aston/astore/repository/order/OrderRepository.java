package ru.aston.astore.repository.order;

import ru.aston.astore.entity.order.Order;
import ru.aston.astore.entity.order.OrderStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<Order> addOrder(Order newOrder);

    boolean addProductsIntoOrder(Collection<UUID> productsIds, UUID orderId);

    Optional<Order> findById(UUID id);

    Collection<UUID> getProductIdsByOrder(UUID id);

    Collection<Order> findByStatus(OrderStatus status);

    boolean updateOrder(Order updatedOrder);

    boolean removeOrder(UUID id);
}
