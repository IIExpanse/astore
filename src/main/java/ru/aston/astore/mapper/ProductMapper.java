package ru.aston.astore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.aston.astore.dto.ProductDto;
import ru.aston.astore.entity.Product;

import java.util.UUID;

@Mapper
public interface ProductMapper {

    Product mapToEntity(ProductDto dto);

    @Mapping(target = "id", source = "id")
    Product mapToEntity(ProductDto dto, UUID id);

    ProductDto mapToDto(Product product);
}
