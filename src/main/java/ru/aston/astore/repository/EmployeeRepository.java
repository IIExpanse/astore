package ru.aston.astore.repository;

import ru.aston.astore.entity.Employee;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends BaseRepository<Employee> {
    Optional<Employee> add(Employee newEmployee);

    Optional<Employee> findById(UUID id);

    Collection<Employee> findByName(String firstName, String lastName);

    boolean update(Employee updatedEmployee);

    boolean remove(UUID id);
}
