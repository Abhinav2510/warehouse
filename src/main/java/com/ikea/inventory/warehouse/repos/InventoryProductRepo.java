package com.ikea.inventory.warehouse.repos;

import com.ikea.inventory.warehouse.entities.InventoryProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InventoryProductRepo extends JpaRepository<InventoryProduct,Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InventoryProduct product SET product.quantity = :possibleQuantity where product.productId = :productId")
    public int updatePossibleQuantityByProductId(long possibleQuantity,long productId);
}
