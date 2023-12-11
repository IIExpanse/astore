package ru.aston.astore.service.impl;

import lombok.RequiredArgsConstructor;
import ru.aston.astore.dto.OrderDto;
import ru.aston.astore.entity.Order;
import ru.aston.astore.entity.OrderStatus;
import ru.aston.astore.mapper.OrderMapper;
import ru.aston.astore.repository.OrderRepository;
import ru.aston.astore.service.OrderService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;

    @Override
    public OrderDto add(OrderDto newOrder) {
        Optional<Order> order = repository.add(
                mapper.mapToEntity(
                        newOrder,
                        UUID.randomUUID(),
                        LocalDateTime.now()));
        if (order.isEmpty()) {
            throw new RuntimeException("Error while adding a new order: " + newOrder);
        }
        return mapper.mapToDto(order.get());
    }

    @Override
    public boolean addProductsIntoOrder(Collection<UUID> productsIds, UUID orderId) {
        return repository.addProductsIntoOrder(productsIds, orderId);
    }

    @Override
    public Optional<OrderDto> findById(UUID id) {
        return repository.findById(id).map(mapper::mapToDto);
    }

    @Override
    public Collection<UUID> getProductIdsByOrder(UUID id) {
        return repository.getProductIdsByOrder(id);
    }

    @Override
    public Collection<OrderDto> findByStatus(OrderStatus status) {
        return repository.findByStatus(status).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean update(OrderDto updatedOrder) {
        return repository.update(mapper.mapToEntity(updatedOrder));
    }

    @Override
    public boolean remove(UUID id) {
        return repository.remove(id);
    }
}
