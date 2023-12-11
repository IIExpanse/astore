package ru.aston.astore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.aston.astore.dto.OrderDto;
import ru.aston.astore.entity.Order;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper
public interface OrderMapper {

    Order mapToEntity(OrderDto dto);

    @Mapping(target = "id", source = "id")
    Order mapToEntity(OrderDto dto, UUID id, LocalDateTime dateTime);

    OrderDto mapToDto(Order order);
}
