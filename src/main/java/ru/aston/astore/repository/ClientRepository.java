package ru.aston.astore.repository;

import ru.aston.astore.entity.Client;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends BaseRepository<Client> {
    Optional<Client> add(Client newClient);

    Optional<Client> findById(UUID id);

    Collection<Client> findByName(String firstName, String lastName);

    boolean update(Client updatedClient);

    boolean remove(UUID id);
}
