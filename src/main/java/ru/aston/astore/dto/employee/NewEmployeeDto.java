package ru.aston.astore.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.aston.astore.entity.employee.EmployeeRole;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NewEmployeeDto {
    private String firstName;
    private String lastName;
    private EmployeeRole role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewEmployeeDto that = (NewEmployeeDto) o;

        if (!firstName.equals(that.firstName)) return false;
        if (!lastName.equals(that.lastName)) return false;
        return role == that.role;
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + role.hashCode();
        return result;
    }
}
