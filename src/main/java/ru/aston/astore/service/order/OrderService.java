package ru.aston.astore.service.order;

import ru.aston.astore.dto.order.NewOrderDto;
import ru.aston.astore.dto.order.OrderDto;
import ru.aston.astore.entity.order.Order;
import ru.aston.astore.entity.order.OrderStatus;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OrderService {
    OrderDto addOrder(NewOrderDto newOrder);

    boolean addProductsIntoOrder(Collection<UUID> productsIds, UUID orderId);

    Optional<OrderDto> findById(UUID id);

    Collection<UUID> getProductIdsByOrder(UUID id);

    Collection<OrderDto> findByStatus(OrderStatus status);

    boolean updateOrder(OrderDto updatedOrder);

    boolean removeOrder(UUID id);
}
