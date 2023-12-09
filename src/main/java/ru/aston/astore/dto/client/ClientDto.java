package ru.aston.astore.dto.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
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

        return id.equals(clientDto.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
