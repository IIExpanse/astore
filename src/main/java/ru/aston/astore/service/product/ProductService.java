package ru.aston.astore.service.product;

import ru.aston.astore.dto.product.ProductDto;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {
    ProductDto addProduct(ProductDto newProduct);

    Optional<ProductDto> findById(UUID id);

    Collection<ProductDto> findByTitle(String title);

    boolean updateProduct(ProductDto updatedProduct);

    boolean removeProduct(UUID id);
}
