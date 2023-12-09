package ru.aston.astore.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.aston.astore.entity.order.OrderStatus;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NewOrderDto {
    private UUID client_id;
    private UUID assignedManager;
    private OrderStatus status;
    private Collection<UUID> products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewOrderDto that = (NewOrderDto) o;

        if (!client_id.equals(that.client_id)) return false;
        if (!Objects.equals(assignedManager, that.assignedManager))
            return false;
        if (status != that.status) return false;
        return products.equals(that.products);
    }

    @Override
    public int hashCode() {
        int result = client_id.hashCode();
        result = 31 * result + (assignedManager != null ? assignedManager.hashCode() : 0);
        result = 31 * result + status.hashCode();
        result = 31 * result + products.hashCode();
        return result;
    }
}
