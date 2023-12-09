package ru.aston.astore.entity.client;

import lombok.Builder;
import lombok.Getter;

import java.util.Collection;
import java.util.UUID;

@Builder
@Getter
public class Client  {
    private final UUID id;
    private final String firstName;
    private final String lastName;
    private final Collection<UUID> madeOrders;

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
