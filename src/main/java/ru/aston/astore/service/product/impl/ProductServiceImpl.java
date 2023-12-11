package ru.aston.astore.service.product.impl;

import lombok.RequiredArgsConstructor;
import ru.aston.astore.dto.product.ProductDto;
import ru.aston.astore.entity.product.Product;
import ru.aston.astore.mapper.product.ProductMapper;
import ru.aston.astore.repository.product.ProductRepository;
import ru.aston.astore.service.product.ProductService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public ProductDto addProduct(ProductDto newProduct) {
        Optional<Product> product = repository.addProduct(mapper.mapToEntity(newProduct, UUID.randomUUID()));
        if (product.isEmpty()) {
            throw new RuntimeException("Error while adding new client: " + newProduct.toString());
        }
        return mapper.mapToDto(product.get());
    }

    @Override
    public Optional<ProductDto> findById(UUID id) {
        return repository.findById(id).map(mapper::mapToDto);
    }

    @Override
    public Collection<ProductDto> findByTitle(String title) {
        return repository.findByTitle(title).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateProduct(ProductDto updatedProduct) {
        return repository.updateProduct(mapper.mapToEntity(updatedProduct));
    }

    @Override
    public boolean removeProduct(UUID id) {
        return repository.removeProduct(id);
    }
}
