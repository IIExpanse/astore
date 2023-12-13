package ru.aston.astore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.aston.astore.dto.ClientDto;
import ru.aston.astore.entity.Client;

import java.util.UUID;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "madeOrders", ignore = true)
    @Mapping(target = "id", source = "id")
    Client mapToEntity(ClientDto dto, UUID id);

    ClientDto mapToDto(Client client);

    Client update(@MappingTarget Client target, ClientDto source);
}
