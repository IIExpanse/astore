package ru.aston.astore.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.aston.astore.dto.EmployeeDto;
import ru.aston.astore.entity.Employee;
import ru.aston.astore.entity.EmployeeRole;
import ru.aston.astore.mapper.EmployeeMapper;
import ru.aston.astore.repository.EmployeeRepository;

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
        EmployeeDto dto = getNewEmployeeDto();
        Employee employee = getDefaultEmployee();

        Mockito.when(repository.add(ArgumentMatchers.any(Employee.class))).thenReturn(Optional.of(employee));
        assertEquals(mapper.mapToDto(employee), service.add(dto));
    }

    @Test
    void throwsExceptionForEmptyAddedReturn() {
        EmployeeDto dto = getNewEmployeeDto();

        Mockito.when(repository.add(ArgumentMatchers.any(Employee.class))).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.add(dto));
    }

    @Test
    void findById() {
        Employee employee = getDefaultEmployee();

        Mockito.when(repository.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(employee));
        assertEquals(Optional.of(mapper.mapToDto(employee)), service.findById(UUID.randomUUID()));
    }

    @Test
    void findByName() {
        Employee employee = getDefaultEmployee();

        Mockito.when(repository.findByName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(List.of(employee));
        assertEquals(List.of(mapper.mapToDto(employee)), service.findByName("John", "Doe"));
    }

    @Test
    void updateEmployee() {
        Employee employee = getDefaultEmployee();

        Mockito.when(repository.update(ArgumentMatchers.any(Employee.class))).thenReturn(true);
        assertTrue(service.update(mapper.mapToDto(employee)));
    }

    @Test
    void removeEmployee() {
        Mockito.when(repository.remove(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        assertTrue(service.remove(UUID.randomUUID()));
    }

    private EmployeeDto getNewEmployeeDto() {
        EmployeeDto dto = new EmployeeDto();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setRole(EmployeeRole.MANAGER);

        return dto;
    }

    private Employee getDefaultEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID())
                .firstName("Jack")
                .lastName("Bond")
                .role(EmployeeRole.MANAGER)
                .build();
    }
}