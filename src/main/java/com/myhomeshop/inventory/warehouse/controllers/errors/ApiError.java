package com.myhomeshop.inventory.warehouse.controllers.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
/**
 * ApiError
 * class to represent exceptions caught by ControllerAdvice {@link InventoryExceptionHandler}
 */
@RequiredArgsConstructor
@Getter
public class ApiError {
    private final HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private final String message;
}
