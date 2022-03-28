package com.myhomeshop.inventory.warehouse.controllers.errors;

import lombok.Getter;
import lombok.Setter;

/**
 *  InventoryException
 *  RuntimeException to handle expected exceptions and Map them to {@link ApiError}
 */
@Getter
@Setter
public class InventoryException extends RuntimeException  {

    private final ErrorCodes errorCode;
    private final String message;

    public InventoryException(ErrorCodes errorCode, String message){
        super(message);
        this.errorCode= errorCode;
        this.message=message;
    }

}
