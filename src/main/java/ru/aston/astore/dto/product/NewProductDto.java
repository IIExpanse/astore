package ru.aston.astore.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.aston.astore.entity.product.ProductType;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NewProductDto {
    private String title;
    private Float price;
    private Float discount;
    private ProductType type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewProductDto that = (NewProductDto) o;

        if (!title.equals(that.title)) return false;
        if (!price.equals(that.price)) return false;
        if (!Objects.equals(discount, that.discount)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + (discount != null ? discount.hashCode() : 0);
        result = 31 * result + type.hashCode();
        return result;
    }
}
