package com.ikea.inventory.warehouse.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikea.inventory.warehouse.controllers.dto.ProductDTO;
import com.ikea.inventory.warehouse.entities.InventoryArticle;
import com.ikea.inventory.warehouse.entities.InventoryProduct;
import com.ikea.inventory.warehouse.entities.ProductArticleDependency;
import com.ikea.inventory.warehouse.services.InventoryService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InventoryProductControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    InventoryService inventoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private InventoryArticle article1, article2;
    private InventoryProduct inventoryProduct1, inventoryProduct2;
    private ProductArticleDependency dependency1Product1, dependency2Product1, dependency1Product2, dependency2Product2;

    @BeforeAll
    void init() {
        article1 = InventoryArticle
                .builder()
                .articleId(1)
                .articleName("Article1")
                .dependantProduct(new ArrayList<>(2))
                .stock(4)
                .build();

        article2 = InventoryArticle
                .builder()
                .articleId(2)
                .articleName("Article2")
                .dependantProduct(new ArrayList<>(2))
                .stock(4)
                .build();

        inventoryProduct1 = InventoryProduct
                .builder()
                .productId(1)
                .productName("Product1")
                .build();

        inventoryProduct2 = InventoryProduct
                .builder()
                .productId(2)
                .productName("Product2")
                .build();

        dependency1Product1 = new ProductArticleDependency();
        dependency1Product1.setArticle(article1);
        dependency1Product1.setRequiredQuantity(2);
        dependency1Product1.setInventoryProduct(inventoryProduct1);
        article1.getDependantProduct().add(dependency1Product1);

        dependency2Product1 = new ProductArticleDependency();
        dependency2Product1.setArticle(article2);
        dependency2Product1.setRequiredQuantity(2);
        dependency2Product1.setInventoryProduct(inventoryProduct1);
        article2.getDependantProduct().add(dependency2Product1);

        inventoryProduct1.setDependantOn(Arrays.asList(dependency1Product1, dependency2Product1));

        dependency1Product2 = new ProductArticleDependency();
        dependency1Product2.setArticle(article1);
        dependency1Product2.setRequiredQuantity(2);
        dependency1Product2.setInventoryProduct(inventoryProduct2);
        article1.getDependantProduct().add(dependency1Product2);

        dependency2Product2 = new ProductArticleDependency();
        dependency2Product2.setArticle(article2);
        dependency2Product2.setRequiredQuantity(2);
        dependency2Product2.setInventoryProduct(inventoryProduct2);
        article2.getDependantProduct().add(dependency2Product2);
        inventoryProduct2.setDependantOn(Arrays.asList(dependency1Product2, dependency2Product2));

    }


    @Test
    public void testProductDTOProductEntityMapping() throws Exception {
        Mockito.when(inventoryService.getAllProducts(0, 1)).thenReturn(Collections.singletonList(inventoryProduct1));

        RequestBuilder productPage0Size1 = getAllRequestBuilder(0,1);

        mockMvc.perform(productPage0Size1)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productName").value("Product1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].quantity").value(0));
    }


    @Test
    public void testGetAllProductPaginationAndSize() throws Exception {
        Mockito.when(inventoryService.getAllProducts(0, 2)).thenReturn(Arrays.asList(inventoryProduct1,inventoryProduct2));

        RequestBuilder productPage0Size1 = getAllRequestBuilder(0,2);

        mockMvc.perform(productPage0Size1)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productName").value("Product1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].quantity").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].productName").value("Product2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].quantity").value(0));
    }

    private RequestBuilder getAllRequestBuilder(int page,int size) {
        return MockMvcRequestBuilders
                .get("/inventory/product")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }


    @TestConfiguration
    static class TestContextConfiguration {
        @Bean
        public ModelMapper bean() {
            return new ModelMapper();
        }

        @Bean(name = "productListType")
        Type typeToken() {
            return new TypeToken<List<ProductDTO>>() {
            }.getType();
        }
    }

}
