package ru.aston.astore.repository.product;


import ru.aston.astore.entity.product.Product;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Optional<Product> addProduct(Product newProduct);

    Optional<Product> findById(UUID id);

    Collection<Product> findByTitle(String title);

    boolean updateProduct(Product updatedProduct);

    boolean removeProduct(UUID id);
}
