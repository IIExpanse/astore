package ru.aston.astore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.aston.astore.dto.OrderDto;
import ru.aston.astore.entity.Order;

import java.time.LocalDateTime;
import java.util.UUID;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "created", source = "created")
    Order mapToEntity(OrderDto dto, UUID id, LocalDateTime created);

    OrderDto mapToDto(Order order);

    @Mapping(target = "target.assignedManager",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    Order update(@MappingTarget Order target, OrderDto src);
}
