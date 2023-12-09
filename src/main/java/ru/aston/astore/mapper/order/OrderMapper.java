package ru.aston.astore.mapper.order;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.aston.astore.dto.order.NewOrderDto;
import ru.aston.astore.dto.order.OrderDto;
import ru.aston.astore.entity.order.Order;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Mapper
public interface OrderMapper {
    Order mapToEntity(NewOrderDto dto, UUID id, LocalDateTime created);

    Order mapToEntity(OrderDto dto);

    OrderDto mapToDto(Order order);
}
