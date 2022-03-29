package com.myhomeshop.inventory.warehouse.controllers.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ProductDTO
 * DTO Object for response object of InventoryProductControllers get and pos mappings
 */

@Data
@NoArgsConstructor
public class ProductDTO {

    private long productId;
    private String productName;
    private long quantity;

}
