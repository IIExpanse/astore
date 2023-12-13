package ru.aston.astore.repository;


import ru.aston.astore.entity.Product;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends BaseRepository<Product> {
    Optional<Product> add(Product newProduct);

    Optional<Product> findById(UUID id);

    Collection<Product> findByTitle(String title);

    boolean update(Product updatedProduct);

    boolean remove(UUID id);
}
