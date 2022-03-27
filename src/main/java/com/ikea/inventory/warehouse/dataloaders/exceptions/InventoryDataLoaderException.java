package com.ikea.inventory.warehouse.dataloaders.exceptions;

/**
 * InventoryDataLoaderException
 * for future usage for making data loaders resilient to exceptions and data errors
 */
public class InventoryDataLoaderException extends RuntimeException{
    public InventoryDataLoaderException(String message){
        super(message);
    }
}
