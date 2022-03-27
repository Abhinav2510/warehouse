package com.ikea.inventory.warehouse.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

/**
 * ProductUpdateDTO
 *  DTO class acting as input model for post mapping of InventoryProductController
 */
@Data
@NoArgsConstructor
public class ProductUpdateDTO {
    @Positive
    private long productId;
    @Positive
    private long sellingQuantity;
}
