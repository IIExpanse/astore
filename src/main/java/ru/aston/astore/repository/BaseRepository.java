package ru.aston.astore.repository;

import java.util.Optional;
import java.util.UUID;

public interface BaseRepository<E> {
    Optional<E> add(E newElement);

    Optional<E> findById(UUID id);

    boolean update(E updatedElement);

    boolean remove(UUID id);
}
