package com.ikea.inventory.warehouse.controllers.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * InventoryExceptionHandler
 *  To handle expected exceptions in Application and Map thgem to Application specific {@link ErrorCodes} which are translated to {@link ApiError}
 */
@ControllerAdvice
public class InventoryExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {InventoryException.class})
    private ResponseEntity<Object> handleException(InventoryException exception){
        switch (exception.getErrorCode()){
            case NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(HttpStatus.NOT_FOUND,exception.getMessage()));
            case NOT_ENOUGH_INVENTORY:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST,exception.getMessage()));
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR,exception.getMessage()));
        }

    }
}
