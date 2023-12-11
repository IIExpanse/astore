package ru.aston.astore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.aston.astore.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
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

        if (!Objects.equals(id, orderDto.id)) return false;
        if (!Objects.equals(client_id, orderDto.client_id)) return false;
        if (!Objects.equals(assignedManager, orderDto.assignedManager))
            return false;
        if (status != orderDto.status) return false;
        if (!Objects.equals(created, orderDto.created)) return false;
        return Objects.equals(products, orderDto.products);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (client_id != null ? client_id.hashCode() : 0);
        result = 31 * result + (assignedManager != null ? assignedManager.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (created != null ? created.hashCode() : 0);
        result = 31 * result + (products != null ? products.hashCode() : 0);
        return result;
    }
}
