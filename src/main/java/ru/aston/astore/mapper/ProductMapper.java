package ru.aston.astore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.aston.astore.dto.ProductDto;
import ru.aston.astore.entity.Product;

import java.util.UUID;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "id", source = "id")
    Product mapToEntity(ProductDto dto, UUID id);

    ProductDto mapToDto(Product product);

    Product update(@MappingTarget Product target, ProductDto source);
}
