package ru.aston.astore.service;

import ru.aston.astore.dto.OrderDto;
import ru.aston.astore.entity.OrderStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OrderService extends BaseService<OrderDto> {
    OrderDto add(OrderDto newOrder);

    boolean addProductsIntoOrder(Collection<UUID> productsIds, UUID orderId);

    Optional<OrderDto> findById(UUID id);

    Collection<UUID> getProductIdsByOrder(UUID id);

    Collection<OrderDto> findByStatus(OrderStatus status);

    boolean update(OrderDto updatedOrder);

    boolean remove(UUID id);
}
