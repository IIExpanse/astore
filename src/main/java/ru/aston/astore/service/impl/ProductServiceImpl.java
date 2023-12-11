package ru.aston.astore.service.impl;

import lombok.RequiredArgsConstructor;
import ru.aston.astore.dto.ProductDto;
import ru.aston.astore.entity.Product;
import ru.aston.astore.mapper.ProductMapper;
import ru.aston.astore.repository.ProductRepository;
import ru.aston.astore.service.ProductService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public ProductDto add(ProductDto newProduct) {
        Optional<Product> product = repository.add(mapper.mapToEntity(newProduct, UUID.randomUUID()));
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
    public boolean update(ProductDto updatedProduct) {
        return repository.update(mapper.mapToEntity(updatedProduct));
    }

    @Override
    public boolean remove(UUID id) {
        return repository.remove(id);
    }
}
