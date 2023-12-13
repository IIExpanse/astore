package ru.aston.astore.service;

import ru.aston.astore.dto.ProductDto;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProductService extends BaseService<ProductDto> {
    ProductDto add(ProductDto newProduct);

    Optional<ProductDto> findById(UUID id);

    Collection<ProductDto> findByTitle(String title);

    boolean update(ProductDto updatedProduct);

    boolean remove(UUID id);
}
