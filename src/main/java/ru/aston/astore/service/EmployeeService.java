package ru.aston.astore.service;

import ru.aston.astore.dto.EmployeeDto;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeService extends BaseService<EmployeeDto> {
    EmployeeDto add(EmployeeDto newEmployee);

    Optional<EmployeeDto> findById(UUID id);

    Collection<EmployeeDto> findByName(String firstName, String lastName);

    boolean update(EmployeeDto updatedEmployee);

    boolean remove(UUID id);
}
