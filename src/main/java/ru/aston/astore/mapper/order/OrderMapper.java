package ru.aston.astore.mapper.order;

import org.mapstruct.Mapper;
import ru.aston.astore.dto.order.OrderDto;
import ru.aston.astore.entity.order.Order;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper
public interface OrderMapper {

    Order mapToEntity(OrderDto dto);

    Order mapToEntity(OrderDto dto, UUID id, LocalDateTime dateTime);

    OrderDto mapToDto(Order order);
}
