package ru.aston.astore.service.impl;

import lombok.RequiredArgsConstructor;
import ru.aston.astore.dto.EmployeeDto;
import ru.aston.astore.entity.Employee;
import ru.aston.astore.mapper.EmployeeMapper;
import ru.aston.astore.repository.EmployeeRepository;
import ru.aston.astore.service.EmployeeService;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Override
    public EmployeeDto add(EmployeeDto newEmployee) {
        Optional<Employee> employee = repository.add(mapper.mapToEntity(newEmployee, UUID.randomUUID()));
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
    public boolean update(EmployeeDto updatedEmployee) {
        Optional<Employee> src = repository.findById(updatedEmployee.getId());
        return src.filter(employee -> repository.update(mapper.update(employee, updatedEmployee))).isPresent();
    }

    @Override
    public boolean remove(UUID id) {
        return repository.remove(id);
    }
}
