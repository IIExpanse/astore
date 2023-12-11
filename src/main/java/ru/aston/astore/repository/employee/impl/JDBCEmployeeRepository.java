package ru.aston.astore.repository.employee.impl;

import lombok.extern.slf4j.Slf4j;
import ru.aston.astore.connection.ConnectionPool;
import ru.aston.astore.entity.employee.Employee;
import ru.aston.astore.entity.employee.EmployeeRole;
import ru.aston.astore.repository.employee.EmployeeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class JDBCEmployeeRepository implements EmployeeRepository {
    private static final String INSERT_EMPLOYEE = "INSERT INTO employees (id, first_name, last_name, role) " +
            "VALUES (?, ?, ?, ?)";
    private static final String FIND_EMPLOYEE_BI_ID = "SELECT * FROM employees WHERE id = ?";
    private static final String FIND_EMPLOYEE_BI_NAME = "SELECT * FROM employees " +
            "WHERE first_name LIKE ? AND last_name LIKE ?";
    private static final String UPDATE_EMPLOYEE = "UPDATE employees SET first_name = ?, last_name = ?, role = ? " +
            "WHERE id = ?";
    private static final String DELETE_EMPLOYEE = "DELETE FROM employees WHERE id = ?";

    @Override
    public Optional<Employee> addEmployee(Employee newEmployee) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(INSERT_EMPLOYEE);
            ps.setObject(1, newEmployee.getId());
            ps.setString(2, newEmployee.getFirstName());
            ps.setString(3, newEmployee.getLastName());
            ps.setString(4, newEmployee.getRole().toString());
            ps.executeUpdate();
            ps.close();

            return findById(newEmployee.getId());

        } catch (SQLException e) {
            log.debug("Error while adding a new employee: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Employee> findById(UUID id) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(FIND_EMPLOYEE_BI_ID);
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();

            Optional<Employee> ans = rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            ps.close();
            return ans;

        } catch (SQLException e) {
            log.debug(String.format("Error while finding an employee with id=%s: %s",
                    id.toString(), e.getMessage()));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Employee> findByName(String firstName, String lastName) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(FIND_EMPLOYEE_BI_NAME);
            ps.setString(1, "%" + firstName + "%");
            ps.setString(2, "%" + lastName + "%");
            ResultSet rs = ps.executeQuery();

            List<Employee> list = new ArrayList<>();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            ps.close();
            return list;

        } catch (SQLException e) {
            log.debug(String.format("Error while finding employees with firstName=%s and lastName=%s: %s",
                    firstName, lastName, e.getMessage()));
        }
        return List.of();
    }

    @Override
    public boolean updateEmployee(Employee updatedEmployee) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(UPDATE_EMPLOYEE);
            ps.setString(1, updatedEmployee.getFirstName());
            ps.setString(2, updatedEmployee.getLastName());
            ps.setString(3, updatedEmployee.getRole().toString());
            ps.setObject(4, updatedEmployee.getId());
            int affectedRows = ps.executeUpdate();
            ps.close();

            return affectedRows > 0;

        } catch (SQLException e) {
            log.debug(String.format("Error while updating an employee with id=%s: %s",
                    updatedEmployee.getId().toString(), e.getMessage()));
        }
        return false;
    }

    @Override
    public boolean removeEmployee(UUID id) {
        try (Connection con = ConnectionPool.getConnection()) {
            PreparedStatement ps = con.prepareStatement(DELETE_EMPLOYEE);
            ps.setObject(1, id);
            int affectedRows = ps.executeUpdate();
            ps.close();

            return affectedRows > 0;

        } catch (SQLException e) {
            log.debug(String.format("Error while removing an employee with id=%s: %s",
                    id.toString(), e.getMessage()));
        }
        return false;
    }

    private Employee mapRow(ResultSet rs) throws SQLException {
        return Employee.builder()
                .id(UUID.fromString(rs.getString(1)))
                .firstName(rs.getString(2))
                .lastName(rs.getString(3))
                .role(EmployeeRole.valueOf(rs.getString(4)))
                .build();
    }
}
