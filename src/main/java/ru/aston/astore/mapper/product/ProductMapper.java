package ru.aston.astore.mapper.product;

import org.mapstruct.Mapper;
import ru.aston.astore.dto.product.ProductDto;
import ru.aston.astore.entity.product.Product;

import java.util.UUID;

@Mapper
public interface ProductMapper {

    Product mapToEntity(ProductDto dto);

    Product mapToEntity(ProductDto dto, UUID id);

    ProductDto mapToDto(Product product);
}
