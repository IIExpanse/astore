package ru.aston.astore.service.order.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Or;
import ru.aston.astore.dto.order.NewOrderDto;
import ru.aston.astore.entity.order.Order;
import ru.aston.astore.entity.order.OrderStatus;
import ru.aston.astore.entity.product.Product;
import ru.aston.astore.mapper.order.OrderMapper;
import ru.aston.astore.repository.order.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderServiceImplTest {

    private OrderServiceImpl service;
    private OrderRepository repository;
    private OrderMapper mapper;

    @BeforeEach
    void setMocks() {
        repository = Mockito.mock(OrderRepository.class);
        mapper = Mappers.getMapper(OrderMapper.class);
        service = new OrderServiceImpl(repository, mapper);
    }

    @Test
    void addOrder() {
        NewOrderDto dto = getNewOrderDto();
        Order order = getDefaultOrder();

        Mockito.when(repository.addOrder(ArgumentMatchers.any(Order.class))).thenReturn(Optional.of(order));
        assertEquals(mapper.mapToDto(order), service.addOrder(dto));
    }

    @Test
    void addProductsIntoOrder() {
        Mockito.when(repository.addProductsIntoOrder(ArgumentMatchers.anyList(), ArgumentMatchers.any(UUID.class)))
                .thenReturn(true);
        assertTrue(service.addProductsIntoOrder(List.of(UUID.randomUUID()), UUID.randomUUID()));
    }

    @Test
    void findById() {
        Order order = getDefaultOrder();

        Mockito.when(repository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(order));
        assertEquals(Optional.of(mapper.mapToDto(order)), service.findById(UUID.randomUUID()));
    }

    @Test
    void getProductIdsByOrder() {
        List<UUID> list = List.of(UUID.randomUUID());

        Mockito.when(repository.getProductIdsByOrder(ArgumentMatchers.any(UUID.class))).thenReturn(list);
        assertEquals(list, service.getProductIdsByOrder(UUID.randomUUID()));
    }

    @Test
    void findByStatus() {
        Order order = getDefaultOrder();

        Mockito.when(repository.findByStatus(ArgumentMatchers.any(OrderStatus.class)))
                .thenReturn(List.of(order));
        assertEquals(List.of(mapper.mapToDto(order)), service.findByStatus(OrderStatus.PENDING));
    }

    @Test
    void updateOrder() {
        Order order = getDefaultOrder();

        Mockito.when(repository.updateOrder(ArgumentMatchers.any(Order.class))).thenReturn(true);
        assertTrue(service.updateOrder(mapper.mapToDto(order)));
    }

    @Test
    void removeOrder() {
        Mockito.when(repository.removeOrder(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        assertTrue(service.removeOrder(UUID.randomUUID()));
    }

    private Order getDefaultOrder() {
        return Order.builder()
                .id(UUID.randomUUID())
                .client_id(UUID.randomUUID())
                .status(OrderStatus.PENDING)
                .created(LocalDateTime.now())
                .products(List.of())
                .build();
    }

    private NewOrderDto getNewOrderDto() {
        NewOrderDto dto = new NewOrderDto();
        dto.setClient_id(UUID.randomUUID());
        dto.setStatus(OrderStatus.PENDING);
        dto.setProducts(List.of(UUID.randomUUID()));

        return dto;
    }
}