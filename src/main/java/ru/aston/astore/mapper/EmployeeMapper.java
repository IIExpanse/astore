package ru.aston.astore.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.aston.astore.dto.EmployeeDto;
import ru.aston.astore.entity.Employee;

import java.util.UUID;

@Mapper
public interface EmployeeMapper {

    Employee mapToEntity(EmployeeDto dto);

    @Mapping(target = "assignedOrders", ignore = true)
    @Mapping(target = "id", source = "id")
    Employee mapToEntity(EmployeeDto dto, UUID id);

    EmployeeDto mapToDto(Employee employee);
}
