package ru.aston.astore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.astore.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDto {
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

        OrderDto orderDto = (OrderDto) o;

        return id.equals(orderDto.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
