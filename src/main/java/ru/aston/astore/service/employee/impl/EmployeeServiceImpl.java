package ru.aston.astore.service.employee.impl;

import lombok.RequiredArgsConstructor;
import ru.aston.astore.dto.employee.EmployeeDto;
import ru.aston.astore.entity.employee.Employee;
import ru.aston.astore.mapper.employee.EmployeeMapper;
import ru.aston.astore.repository.employee.EmployeeRepository;
import ru.aston.astore.service.employee.EmployeeService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Override
    public EmployeeDto addEmployee(EmployeeDto newEmployee) {
        Optional<Employee> employee = repository.addEmployee(mapper.mapToEntity(newEmployee, UUID.randomUUID()));
        if (employee.isEmpty()) {
            throw new RuntimeException("Error while adding new client: " + newEmployee.toString());
        }
        return mapper.mapToDto(employee.get());
    }

    @Override
    public Optional<EmployeeDto> findById(UUID id) {
        return repository.findById(id).map(mapper::mapToDto);
    }

    @Override
    public Collection<EmployeeDto> findByName(String firstName, String lastName) {
        return repository.findByName(firstName, lastName).stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateEmployee(EmployeeDto updatedEmployee) {
        return repository.updateEmployee(mapper.mapToEntity(updatedEmployee));
    }

    @Override
    public boolean removeEmployee(UUID id) {
        return repository.removeEmployee(id);
    }
}
