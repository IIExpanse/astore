package ru.aston.astore.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.aston.astore.dto.EmployeeDto;
import ru.aston.astore.entity.Employee;
import ru.aston.astore.mapper.EmployeeMapper;
import ru.aston.astore.repository.EmployeeRepository;
import ru.aston.astore.util.ObjectsFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeServiceImplTest {
    private EmployeeServiceImpl service;
    private EmployeeRepository repository;
    private EmployeeMapper mapper;

    @BeforeEach
    void setMocks() {
        repository = Mockito.mock(EmployeeRepository.class);
        mapper = Mappers.getMapper(EmployeeMapper.class);
        service = new EmployeeServiceImpl(repository, mapper);
    }

    @Test
    void addEmployee() {
        EmployeeDto dto = ObjectsFactory.getEmployeeDto();
        Employee employee = ObjectsFactory.getDefaultEmployee();

        Mockito.when(repository.add(ArgumentMatchers.any(Employee.class))).thenReturn(Optional.of(employee));
        assertEquals(mapper.mapToDto(employee), service.add(dto));
    }

    @Test
    void throwsExceptionForEmptyAddedReturn() {
        EmployeeDto dto = ObjectsFactory.getEmployeeDto();

        Mockito.when(repository.add(ArgumentMatchers.any(Employee.class))).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.add(dto));
    }

    @Test
    void findById() {
        Employee employee = ObjectsFactory.getDefaultEmployee();

        Mockito.when(repository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(employee));
        assertEquals(Optional.of(mapper.mapToDto(employee)), service.findById(UUID.randomUUID()));
    }

    @Test
    void findByName() {
        Employee employee = ObjectsFactory.getDefaultEmployee();

        Mockito.when(repository.findByName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(List.of(employee));
        assertEquals(List.of(mapper.mapToDto(employee)), service.findByName("John", "Doe"));
    }

    @Test
    void updateEmployee() {
        Employee employee = ObjectsFactory.getDefaultEmployee();

        Mockito.when(repository.update(ArgumentMatchers.any(Employee.class))).thenReturn(true);
        assertTrue(service.update(mapper.mapToDto(employee)));
    }

    @Test
    void removeEmployee() {
        Mockito.when(repository.remove(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        assertTrue(service.remove(UUID.randomUUID()));
    }
}