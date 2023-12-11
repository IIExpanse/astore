package ru.aston.astore.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Getter
@Builder
public class Order {
    private final UUID id;
    private final UUID client_id;
    private final UUID assignedManager;
    private final OrderStatus status;
    private final LocalDateTime created;
    private final Collection<UUID> products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        return id.equals(order.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
