package ru.aston.astore.service.employee;

import ru.aston.astore.dto.employee.EmployeeDto;
import ru.aston.astore.dto.employee.NewEmployeeDto;
import ru.aston.astore.entity.employee.Employee;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeService {
    EmployeeDto addEmployee(NewEmployeeDto newEmployee);

    Optional<EmployeeDto> findById(UUID id);

    Collection<EmployeeDto> findByName(String firstName, String lastName);

    boolean updateEmployee(EmployeeDto updatedEmployee);

    boolean removeEmployee(UUID id);
}
