package ru.aston.astore.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Builder
@Getter
@Setter
public class Order {
    private UUID id;
    private UUID client_id;
    private UUID assignedManager;
    private OrderStatus status;
    private LocalDateTime created;
    private Collection<UUID> products;

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
