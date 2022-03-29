package com.myhomeshop.inventory.warehouse.controllers.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * ProductUpdateDTO
 *  DTO class acting as input model for post mapping of InventoryProductController
 */
@Data
@NoArgsConstructor
public class ProductUpdateDTO {
    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Long sellingQuantity;
}
