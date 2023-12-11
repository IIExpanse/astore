package ru.aston.astore.service.employee;

import ru.aston.astore.dto.employee.EmployeeDto;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeService {
    EmployeeDto addEmployee(EmployeeDto newEmployee);

    Optional<EmployeeDto> findById(UUID id);

    Collection<EmployeeDto> findByName(String firstName, String lastName);

    boolean updateEmployee(EmployeeDto updatedEmployee);

    boolean removeEmployee(UUID id);
}
