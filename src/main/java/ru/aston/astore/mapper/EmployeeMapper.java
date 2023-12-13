package ru.aston.astore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.aston.astore.dto.EmployeeDto;
import ru.aston.astore.entity.Employee;

import java.util.UUID;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    @Mapping(target = "assignedOrders", ignore = true)
    @Mapping(target = "id", source = "id")
    Employee mapToEntity(EmployeeDto dto, UUID id);

    EmployeeDto mapToDto(Employee employee);

    Employee update(@MappingTarget Employee target, EmployeeDto source);
}
