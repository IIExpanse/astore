package ru.aston.astore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private Collection<UUID> madeOrders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientDto clientDto = (ClientDto) o;

        if (!Objects.equals(id, clientDto.id)) return false;
        if (!Objects.equals(firstName, clientDto.firstName)) return false;
        if (!Objects.equals(lastName, clientDto.lastName)) return false;
        return Objects.equals(madeOrders, clientDto.madeOrders);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (madeOrders != null ? madeOrders.hashCode() : 0);
        return result;
    }
}
