package ru.aston.astore.mapper.employee;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.aston.astore.dto.employee.EmployeeDto;
import ru.aston.astore.dto.employee.NewEmployeeDto;
import ru.aston.astore.entity.employee.Employee;

import java.util.UUID;

@Mapper
public interface EmployeeMapper {
    @Mapping(target = "assignedOrders", ignore = true)
    Employee mapToEntity(NewEmployeeDto dto, UUID id);

    Employee mapToEntity(EmployeeDto dto);

    EmployeeDto mapToDto(Employee employee);
}
