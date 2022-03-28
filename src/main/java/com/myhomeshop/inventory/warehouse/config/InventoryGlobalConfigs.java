package com.myhomeshop.inventory.warehouse.config;

import com.myhomeshop.inventory.warehouse.controllers.dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;

/**
 * InventoryGlobalConfigs
 * All configs which are relevant for application yet not significant enough to go in seperate classes
 */
@Component
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
