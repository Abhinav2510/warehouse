package com.ikea.inventory.warehouse.config;

import com.ikea.inventory.warehouse.controllers.dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

@Component
/**
 * InventoryGlobalConfigs
 * All configs which are relevant for application yet not significant enough to go in seperate classes
 */
public class InventoryGlobalConfigs {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean(name = "productListType")
    Type typeToken() {
        return new TypeToken<List<ProductDTO>>() {
        }.getType();
    }
}
