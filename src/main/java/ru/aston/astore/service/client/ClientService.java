package ru.aston.astore.service.client;

import ru.aston.astore.dto.client.ClientDto;
import ru.aston.astore.dto.client.NewClientDto;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ClientService {
    ClientDto addClient(NewClientDto newClient);

    Optional<ClientDto> findById(UUID id);

    Collection<ClientDto> findByName(String firstName, String lastName);

    boolean updateClient(ClientDto updatedClient);

    boolean removeClient(UUID id);
}
