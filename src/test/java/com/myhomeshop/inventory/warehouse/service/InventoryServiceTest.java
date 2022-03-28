package com.myhomeshop.inventory.warehouse.service;

import com.myhomeshop.inventory.warehouse.controllers.errors.InventoryException;
import com.myhomeshop.inventory.warehouse.entities.InventoryArticle;
import com.myhomeshop.inventory.warehouse.entities.InventoryProduct;
import com.myhomeshop.inventory.warehouse.entities.ProductArticleDependency;
import com.myhomeshop.inventory.warehouse.repos.InventoryArticleRepo;
import com.myhomeshop.inventory.warehouse.repos.InventoryProductRepo;
import com.myhomeshop.inventory.warehouse.services.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Captor
    ArgumentCaptor<Long> quantityCaptor;
    @Captor
    ArgumentCaptor<Long> productIdCaptor;
    @Mock
    InventoryArticleRepo inventoryArticleRepo;
    @Mock
    InventoryProductRepo inventoryProductRepo;

    @InjectMocks
    InventoryService inventoryService;

    private InventoryArticle article1,article2;
    private InventoryProduct inventoryProduct1, inventoryProduct2;
    private ProductArticleDependency dependency1Product1,dependency2Product1,dependency1Product2,dependency2Product2;

    @BeforeEach
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
    public void test_InsertProductAndDependencies_Test_Quantities() {
        Mockito.when(inventoryProductRepo.saveAndFlush(Mockito.argThat(m -> m.getProductName().equals("Product1")))).thenReturn(inventoryProduct1);

        inventoryService.insertProductAndDependencies(inventoryProduct1);

        Mockito.verify(inventoryProductRepo, Mockito.times(2)).updatePossibleQuantityByProductId(quantityCaptor.capture(), productIdCaptor.capture());
        List<Long> quantityArgs = quantityCaptor.getAllValues();
        List<Long> productIdArgs = productIdCaptor.getAllValues();

        assertTrue(quantityArgs.containsAll(Arrays.asList(2L,2L)));
        assertTrue(productIdArgs.containsAll(Arrays.asList(1L,2L)));

    }

    @Test
    public void test_updateProductSales_Test_InSufficientArticleQuantities() {

        article1.setStock(2);
        Mockito.when(inventoryProductRepo.findById(Mockito.eq(1L))).thenReturn(Optional.of(inventoryProduct1));
        Mockito.when(inventoryArticleRepo.findById(Mockito.eq(1L))).thenReturn(Optional.of(article1));

        assertThrows(InventoryException.class,()->inventoryService.updateProductSales(1,2));


    }


}
