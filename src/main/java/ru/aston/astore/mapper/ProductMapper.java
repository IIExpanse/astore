package ru.aston.astore.mapper;

import org.mapstruct.Mapper;
import ru.aston.astore.dto.ProductDto;
import ru.aston.astore.entity.Product;

import java.util.UUID;

@Mapper
public interface ProductMapper {

    Product mapToEntity(ProductDto dto);

    Product mapToEntity(ProductDto dto, UUID id);

    ProductDto mapToDto(Product product);
}
