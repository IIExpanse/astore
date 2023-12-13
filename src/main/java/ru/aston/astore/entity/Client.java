package ru.aston.astore.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.UUID;

@Builder
@Getter
@Setter
public class Client  {
    private UUID id;
    private String firstName;
    private String lastName;
    private Collection<UUID> madeOrders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        return id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
