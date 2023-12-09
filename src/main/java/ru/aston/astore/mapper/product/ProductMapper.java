package ru.aston.astore.mapper.product;

import org.mapstruct.Mapper;
import ru.aston.astore.dto.product.NewProductDto;
import ru.aston.astore.dto.product.ProductDto;
import ru.aston.astore.entity.product.Product;

import java.util.UUID;

@Mapper
public interface ProductMapper {
    Product mapToEntity(NewProductDto dto, UUID id);

    Product mapToEntity(ProductDto dto);

    ProductDto mapToDto(Product product);
}
