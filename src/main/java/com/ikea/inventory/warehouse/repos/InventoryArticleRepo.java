package com.ikea.inventory.warehouse.repos;

import com.ikea.inventory.warehouse.entities.InventoryArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryArticleRepo extends JpaRepository<InventoryArticle,Long> {
}
