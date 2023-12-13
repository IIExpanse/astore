package ru.aston.astore.service;

import java.util.Optional;
import java.util.UUID;

public interface BaseService<E> {
    E add(E newElement);

    Optional<E> findById(UUID id);

    boolean update(E updatedElement);

    boolean remove(UUID id);
}
