package ru.aston.astore.service.impl;

import lombok.RequiredArgsConstructor;
import ru.aston.astore.dto.ClientDto;
import ru.aston.astore.entity.Client;
import ru.aston.astore.mapper.ClientMapper;
import ru.aston.astore.repository.ClientRepository;
import ru.aston.astore.service.ClientService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository repository;
    private final ClientMapper mapper;

    @Override
    public ClientDto add(ClientDto newClient) {
        Optional<Client> client = repository.add(mapper.mapToEntity(newClient, UUID.randomUUID()));
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
    public boolean update(ClientDto updatedClient) {
        return repository.update(mapper.mapToEntity(updatedClient));
    }

    @Override
    public boolean remove(UUID id) {
        return repository.remove(id);
    }
}
