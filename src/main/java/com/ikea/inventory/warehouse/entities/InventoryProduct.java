package com.ikea.inventory.warehouse.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "INVENTOTRY_PRODUCTS")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"dependantOn"})
public class InventoryProduct {
    @Id
    @GeneratedValue
    private long productId;
    @Size(min = 3, max = 255, message = "Product name must be 3 to 255 charactes")
    private String productName;

    @OneToMany(mappedBy = "inventoryProduct",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<ProductArticleDependency> dependantOn;

    @PositiveOrZero
    private long quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryProduct that = (InventoryProduct) o;
        return productId == that.productId &&
                Objects.equals(productName, that.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName);
    }
}
