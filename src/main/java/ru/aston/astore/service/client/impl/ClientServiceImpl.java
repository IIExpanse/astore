package ru.aston.astore.service.client.impl;

import lombok.RequiredArgsConstructor;
import ru.aston.astore.dto.client.ClientDto;
import ru.aston.astore.dto.client.NewClientDto;
import ru.aston.astore.entity.client.Client;
import ru.aston.astore.mapper.client.ClientMapper;
import ru.aston.astore.repository.client.ClientRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ClientServiceImpl implements ru.aston.astore.service.client.ClientService {
    private final ClientRepository repository;
    private final ClientMapper mapper;

    @Override
    public ClientDto addClient(NewClientDto newClient) {
        Optional<Client> client = repository.addClient(mapper.mapToEntity(newClient, UUID.randomUUID()));
        if (client.isEmpty()) {
            throw new RuntimeException("Error while adding new client: " + newClient.toString());
        }
        return mapper.mapToDto(client.get());
    }

    @Override
    public Optional<ClientDto> findById(UUID id) {
        return repository.findById(id).map(mapper::mapToDto);
    }

    @Override
    public Collection<ClientDto> findByName(String firstName, String lastName) {
        return repository.findByName(firstName, lastName).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateClient(ClientDto updatedClient) {
        return repository.updateClient(mapper.mapToEntity(updatedClient));
    }

    @Override
    public boolean removeClient(UUID id) {
        return repository.removeClient(id);
    }
}
