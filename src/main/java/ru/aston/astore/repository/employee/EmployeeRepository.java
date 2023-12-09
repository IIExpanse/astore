package ru.aston.astore.repository.employee;

import ru.aston.astore.entity.employee.Employee;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository {
    Optional<Employee> addEmployee(Employee newEmployee);

    Optional<Employee> findById(UUID id);

    Collection<Employee> findByName(String firstName, String lastName);

    boolean updateEmployee(Employee updatedEmployee);

    boolean removeEmployee(UUID id);
}
