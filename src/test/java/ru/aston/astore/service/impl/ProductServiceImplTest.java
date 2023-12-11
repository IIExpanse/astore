package ru.aston.astore.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.aston.astore.dto.ProductDto;
import ru.aston.astore.entity.Product;
import ru.aston.astore.entity.ProductType;
import ru.aston.astore.mapper.ProductMapper;
import ru.aston.astore.repository.ProductRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductServiceImplTest {
    private ProductServiceImpl service;
    private ProductRepository repository;
    private ProductMapper mapper;

    @BeforeEach
    void setMocks() {
        repository = Mockito.mock(ProductRepository.class);
        mapper = Mappers.getMapper(ProductMapper.class);
        service = new ProductServiceImpl(repository, mapper);
    }

    @Test
    void addProduct() {
        ProductDto dto = getNewProductDto();
        Product product = getDefaultProduct();

        Mockito.when(repository.add(ArgumentMatchers.any(Product.class))).thenReturn(Optional.of(product));
        assertEquals(mapper.mapToDto(product), service.add(dto));
    }

    @Test
    void throwsExceptionForEmptyAddedReturn() {
        ProductDto dto = getNewProductDto();

        Mockito.when(repository.add(ArgumentMatchers.any(Product.class))).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.add(dto));
    }

    @Test
    void findById() {
        Product product = getDefaultProduct();

        Mockito.when(repository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(product));
        assertEquals(Optional.of(mapper.mapToDto(product)), service.findById(UUID.randomUUID()));
    }

    @Test
    void findByTitle() {
        Product product = getDefaultProduct();

        Mockito.when(repository.findByTitle(ArgumentMatchers.anyString()))
                .thenReturn(List.of(product));
        assertEquals(List.of(mapper.mapToDto(product)), service.findByTitle("Wooden chair"));
    }

    @Test
    void updateProduct() {
        Product product = getDefaultProduct();

        Mockito.when(repository.update(ArgumentMatchers.any(Product.class))).thenReturn(true);
        assertTrue(service.update(mapper.mapToDto(product)));
    }

    @Test
    void removeProduct() {
        Mockito.when(repository.remove(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        assertTrue(service.remove(UUID.randomUUID()));
    }

    private ProductDto getNewProductDto() {
        ProductDto dto = new ProductDto();
        dto.setTitle("Wooden chair");
        dto.setPrice(14.5f);
        dto.setType(ProductType.FURNITURE);

        return dto;
    }

    private Product getDefaultProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
                .title("Wooden chair")
                .price(9.5f)
                .discount(null)
                .type(ProductType.FURNITURE)
                .build();
    }
}