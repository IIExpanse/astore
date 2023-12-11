package ru.aston.astore.repository.impl;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aston.astore.connection.ConnectionPool;
import ru.aston.astore.entity.Employee;
import ru.aston.astore.entity.EmployeeRole;
import ru.aston.astore.properties.TestProperties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JDBCEmployeeRepositoryTest {
    private JDBCEmployeeRepository repository;
    private static String schema;

    @BeforeAll
    static void setTestDataSource() throws IOException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(TestProperties.testDatabaseURL);
        ConnectionPool.setDataSource(dataSource);

        try (BufferedReader br = new BufferedReader(new FileReader(ConnectionPool.testSchemaPath))) {
            schema = br.lines().collect(Collectors.joining());
        }
    }

    @AfterAll
    static void clearDataSource() {
        ConnectionPool.clearDataSource();
    }

    @BeforeEach
    void refresh() throws SQLException {
        try (Connection con = ConnectionPool.getConnection()) {
            con.prepareStatement("DROP ALL OBJECTS").execute();
            con.prepareStatement(schema).execute();
        }
        repository = new JDBCEmployeeRepository();
    }

    @Test
    void addAndFindEmployee() {
        Employee employee = getDefaultEmployee();
        Employee returnedEmployee = repository.add(employee).orElseThrow();
        assertEquals(employee, returnedEmployee);
    }

    @Test
    void findByName() {
        Employee employee = getDefaultEmployee();
        repository.add(employee);
        assertTrue(repository.findByName(employee.getFirstName(), employee.getLastName())
                .contains(employee));
        assertTrue(repository.findByName(employee.getFirstName(), "")
                .contains(employee));
        assertTrue(repository.findByName("", employee.getLastName())
                .contains(employee));
        assertTrue(repository.findByName("", employee.getLastName().substring(0, 1))
                .contains(employee));
    }

    @Test
    void updateEmployee() {
        Employee employee = getDefaultEmployee();
        repository.add(employee);
        employee = Employee.builder()
                .id(employee.getId())
                .firstName("Bob")
                .lastName("Smith")
                .role(EmployeeRole.MANAGER)
                .build();
        assertTrue(repository.update(employee));

        Employee returnedEmployee = repository.findById(employee.getId()).orElseThrow();
        assertEquals(employee.getFirstName(), returnedEmployee.getFirstName());
        assertEquals(employee.getLastName(), returnedEmployee.getLastName());
    }

    @Test
    void removeEmployee() {
        Employee employee = getDefaultEmployee();
        repository.add(employee);
        assertTrue(repository.remove(employee.getId()));
        assertTrue(repository.findById(employee.getId()).isEmpty());
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