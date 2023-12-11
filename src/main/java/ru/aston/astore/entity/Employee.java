package ru.aston.astore.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.Collection;
import java.util.UUID;

@Builder
@Getter
public class Employee {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final EmployeeRole role;
    private final Collection<UUID> assignedOrders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        return id.equals(employee.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
