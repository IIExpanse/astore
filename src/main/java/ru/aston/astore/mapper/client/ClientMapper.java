package ru.aston.astore.mapper.client;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.aston.astore.dto.client.ClientDto;
import ru.aston.astore.entity.client.Client;

import java.util.UUID;

@Mapper
public interface ClientMapper {

    Client mapToEntity(ClientDto dto);

    @Mapping(target = "madeOrders", ignore = true)
    Client mapToEntity(ClientDto dto, UUID id);

    ClientDto mapToDto(Client client);
}
