package ru.aston.astore.service.client.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.aston.astore.dto.client.ClientDto;
import ru.aston.astore.entity.client.Client;
import ru.aston.astore.mapper.client.ClientMapper;
import ru.aston.astore.repository.client.ClientRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientServiceImplTest {
    private ClientServiceImpl service;
    private ClientRepository repository;
    private ClientMapper mapper;

    @BeforeEach
    void setMocks() {
        repository = Mockito.mock(ClientRepository.class);
        mapper = Mappers.getMapper(ClientMapper.class);
        service = new ClientServiceImpl(repository, mapper);
    }

    @Test
    void addClient() {
        ClientDto dto = getClientDto();
        Client client = getDefaultClient();

        Mockito.when(repository.addClient(ArgumentMatchers.any(Client.class))).thenReturn(Optional.of(client));
        assertEquals(mapper.mapToDto(client), service.addClient(dto));
    }

    @Test
    void throwsExceptionForEmptyAddedReturn() {
        ClientDto dto = getClientDto();

        Mockito.when(repository.addClient(ArgumentMatchers.any(Client.class))).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.addClient(dto));
    }

    @Test
    void findById() {
        Client client = getDefaultClient();

        Mockito.when(repository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(client));
        assertEquals(Optional.of(mapper.mapToDto(client)), service.findById(UUID.randomUUID()));
    }

    @Test
    void findByName() {
        Client client = getDefaultClient();

        Mockito.when(repository.findByName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(List.of(client));
        assertEquals(List.of(mapper.mapToDto(client)), service.findByName("John", "Doe"));
    }

    @Test
    void updateClient() {
        Client client = getDefaultClient();

        Mockito.when(repository.updateClient(ArgumentMatchers.any(Client.class))).thenReturn(true);
        assertTrue(service.updateClient(mapper.mapToDto(client)));
    }

    @Test
    void removeClient() {
        Mockito.when(repository.removeClient(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        assertTrue(service.removeClient(UUID.randomUUID()));
    }

    private Client getDefaultClient() {
        return Client.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    private ClientDto getClientDto() {
        ClientDto dto = new ClientDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");

        return dto;
    }
}