package ru.aston.astore.repository.client;

import ru.aston.astore.entity.client.Client;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Optional<Client> addClient(Client newClient);

    Optional<Client> findById(UUID id);

    Collection<Client> findByName(String firstName, String lastName);

    boolean updateClient(Client updatedClient);

    boolean removeClient(UUID id);
}
