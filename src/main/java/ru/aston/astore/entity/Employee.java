package ru.aston.astore.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;

@Builder
@Getter
@Setter
public class Employee {
    private UUID id;
    private String firstName;
    private String lastName;
    private EmployeeRole role;
    private Collection<UUID> assignedOrders;

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
