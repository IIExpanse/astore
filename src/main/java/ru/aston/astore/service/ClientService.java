package ru.aston.astore.service;

import ru.aston.astore.dto.ClientDto;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ClientService extends BaseService<ClientDto> {
    ClientDto add(ClientDto newClient);

    Optional<ClientDto> findById(UUID id);

    Collection<ClientDto> findByName(String firstName, String lastName);

    boolean update(ClientDto updatedClient);

    boolean remove(UUID id);
}
