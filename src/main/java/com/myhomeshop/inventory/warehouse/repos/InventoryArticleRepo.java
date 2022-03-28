package com.myhomeshop.inventory.warehouse.repos;

import com.myhomeshop.inventory.warehouse.entities.InventoryArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InventoryArticleRepo extends JpaRepository<InventoryArticle,Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE InventoryArticle SET stock = stock - :sellingQuantity where articleId=:articleId ")
    void updateArticleStockById(long articleId,long sellingQuantity);
}
