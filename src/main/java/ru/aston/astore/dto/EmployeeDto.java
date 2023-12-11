package ru.aston.astore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.astore.entity.EmployeeRole;

import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EmployeeDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private EmployeeRole role;
    private Collection<UUID> assignedOrders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmployeeDto that = (EmployeeDto) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
