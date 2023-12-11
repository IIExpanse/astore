package ru.aston.astore.repository;

import ru.aston.astore.entity.Order;
import ru.aston.astore.entity.OrderStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends BaseRepository<Order> {
    Optional<Order> add(Order newOrder);

    boolean addProductsIntoOrder(Collection<UUID> productsIds, UUID orderId);

    Optional<Order> findById(UUID id);

    Collection<UUID> getProductIdsByOrder(UUID id);

    Collection<Order> findByStatus(OrderStatus status);

    boolean update(Order updatedOrder);

    boolean remove(UUID id);
}
