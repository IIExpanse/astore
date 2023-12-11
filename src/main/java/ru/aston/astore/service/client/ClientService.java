package ru.aston.astore.service.client;

import ru.aston.astore.dto.client.ClientDto;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ClientService {
    ClientDto addClient(ClientDto newClient);

    Optional<ClientDto> findById(UUID id);

    Collection<ClientDto> findByName(String firstName, String lastName);

    boolean updateClient(ClientDto updatedClient);

    boolean removeClient(UUID id);
}
