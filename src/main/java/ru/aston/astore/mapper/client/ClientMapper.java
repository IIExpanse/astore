package ru.aston.astore.mapper.client;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.aston.astore.dto.client.ClientDto;
import ru.aston.astore.dto.client.NewClientDto;
import ru.aston.astore.entity.client.Client;

import java.util.UUID;

@Mapper
public interface ClientMapper {
    @Mapping(target = "madeOrders", ignore = true)
    Client mapToEntity(NewClientDto dto, UUID id);

    Client mapToEntity(ClientDto dto);

    ClientDto mapToDto(Client client);
}
