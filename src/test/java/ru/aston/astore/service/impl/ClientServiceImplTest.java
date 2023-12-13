package ru.aston.astore.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.aston.astore.dto.ClientDto;
import ru.aston.astore.entity.Client;
import ru.aston.astore.mapper.ClientMapper;
import ru.aston.astore.repository.ClientRepository;
import ru.aston.astore.util.ObjectsFactory;

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
        ClientDto dto = ObjectsFactory.getClientDto();
        Client client = ObjectsFactory.getDefaultClient();

        Mockito.when(repository.add(ArgumentMatchers.any(Client.class))).thenReturn(Optional.of(client));
        assertEquals(mapper.mapToDto(client), service.add(dto));
    }

    @Test
    void throwsExceptionForEmptyAddedReturn() {
        ClientDto dto = ObjectsFactory.getClientDto();

        Mockito.when(repository.add(ArgumentMatchers.any(Client.class))).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.add(dto));
    }

    @Test
    void findById() {
        Client client = ObjectsFactory.getDefaultClient();

        Mockito.when(repository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(client));
        assertEquals(Optional.of(mapper.mapToDto(client)), service.findById(UUID.randomUUID()));
    }

    @Test
    void findByName() {
        Client client = ObjectsFactory.getDefaultClient();

        Mockito.when(repository.findByName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(List.of(client));
        assertEquals(List.of(mapper.mapToDto(client)), service.findByName("John", "Doe"));
    }

    @Test
    void updateClient() {
        Client client = ObjectsFactory.getDefaultClient();

        Mockito.when(repository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(client));
        Mockito.when(repository.update(ArgumentMatchers.any(Client.class))).thenReturn(true);
        assertTrue(service.update(mapper.mapToDto(client)));
    }

    @Test
    void removeClient() {
        Mockito.when(repository.remove(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        assertTrue(service.remove(UUID.randomUUID()));
    }
}