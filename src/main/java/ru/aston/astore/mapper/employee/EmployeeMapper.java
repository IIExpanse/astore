package ru.aston.astore.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.aston.astore.dto.employee.EmployeeDto;
import ru.aston.astore.entity.employee.Employee;

import java.util.UUID;

@Mapper
public interface EmployeeMapper {

    Employee mapToEntity(EmployeeDto dto);

    @Mapping(target = "assignedOrders", ignore = true)
    Employee mapToEntity(EmployeeDto dto, UUID id);

    EmployeeDto mapToDto(Employee employee);
}
