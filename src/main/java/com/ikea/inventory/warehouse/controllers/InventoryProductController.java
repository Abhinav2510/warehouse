package com.ikea.inventory.warehouse.controllers;

import com.ikea.inventory.warehouse.controllers.dto.ProductDTO;
import com.ikea.inventory.warehouse.controllers.dto.ProductUpdateDTO;
import com.ikea.inventory.warehouse.controllers.errors.ErrorCodes;
import com.ikea.inventory.warehouse.controllers.errors.InventoryException;
import com.ikea.inventory.warehouse.services.InventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.List;

/**
 * InventoryProductController
 * path /inventory/product
 * Allows to get all products in Inventory and
 * also sells products from available inventory
 *
 */

@Slf4j
@RestController
@RequestMapping(path = "/inventory/product")
@AllArgsConstructor
public class InventoryProductController {

    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;
    @Qualifier("productListType")
    private final Type productListType;


    /**
     * Gets all the products from Inventory
     * @param page page defaults to 0
     * @param size size of page defaults to 10
     * @return List of all products in page
     */
    @GetMapping
    public List<ProductDTO> getAllProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return modelMapper.map(inventoryService.getAllProducts(page,size), productListType);
    }

    /**
     * Allows to sell product in specified quantity. Automatically updates available stock
     * not only for the products but also other products which may have been affected by
     * sale of Article as part of product
     *
     * @param productUpdateDTO DTO object containing productId and sellingQuantity
     * @return updated product with recalculated available quantity
     */
    @PostMapping
    public ProductDTO updateProduct(@RequestBody @Valid ProductUpdateDTO productUpdateDTO) {
        boolean updateProductAndInventorySuccess = inventoryService.updateProductSales(productUpdateDTO.getProductId(), productUpdateDTO.getSellingQuantity());
        if (!updateProductAndInventorySuccess) {
            log.warn("Selling of product was not successful {} ",productUpdateDTO.getProductId());
            throw new InventoryException(ErrorCodes.UNKNOWN, "Something went wrong while trying to update product quantity");
        }
        return modelMapper.map(inventoryService.findProductById(productUpdateDTO.getProductId()), ProductDTO.class);

    }
}
