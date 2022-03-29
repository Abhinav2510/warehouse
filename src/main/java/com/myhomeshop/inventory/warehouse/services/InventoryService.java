package com.myhomeshop.inventory.warehouse.services;

import com.myhomeshop.inventory.warehouse.controllers.errors.ErrorCodes;
import com.myhomeshop.inventory.warehouse.controllers.errors.InventoryException;
import com.myhomeshop.inventory.warehouse.entities.InventoryArticle;
import com.myhomeshop.inventory.warehouse.entities.InventoryProduct;
import com.myhomeshop.inventory.warehouse.repos.InventoryArticleRepo;
import com.myhomeshop.inventory.warehouse.repos.InventoryProductRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * InventoryService
 * contains all the business logic for dealing with products and articles
 * as well their dependencies with each others.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class InventoryService {

    private final InventoryArticleRepo inventoryItemRepo;
    private final InventoryProductRepo inventoryProductRepo;

    /**
     * Insert {@link InventoryArticle} into database
     * @param inventoryArticle article to insert in database
     */
    public InventoryArticle insertArticle(@Valid InventoryArticle inventoryArticle) {
       return inventoryItemRepo.saveAndFlush(inventoryArticle);
    }

    /**
     * Get article by id from database
     * @param articleId ID to search for
     * @return Article from database or else null
     */
    public InventoryArticle findByArticleId(long articleId) {
        return inventoryItemRepo.findById(articleId).orElse(null);
    }


    /**
     * gets all the {@link InventoryProduct} from database with pagination support
     * @param page page number
     * @param size number of results in one page
     * @return list of {@link InventoryProduct}
     */
    public List<InventoryProduct> getAllProducts(int page, int size) {

        return inventoryProductRepo.findAll(PageRequest.of(page, size)).getContent();
    }

    /**
     * Inserts products and its dependencies with articles in database
     * @param product product with all dependencies to insert
     */
    @Transactional
    public void insertProductAndDependencies(@Valid InventoryProduct product) {
        InventoryProduct savedInventoryProduct = inventoryProductRepo.saveAndFlush(product);
        updatePossibleQuantityForRelatedProduct(savedInventoryProduct);
    }

    /**
     * find {@link InventoryProduct} by id
     * @param productId productId
     * @return InventoryProduct
     */
    public InventoryProduct findProductById(long productId) {
        return inventoryProductRepo.findById(productId).orElse(null);
    }

    /**
     * Updates the possible quantity for affected products which may depend on same
     * inventory article as the product provided as input
     * @param product for which quantity is changing by sell or update
     */
    public void updatePossibleQuantityForRelatedProduct(@Valid InventoryProduct product) {
        Set<InventoryProduct> productsMarkedForStockChange = new HashSet<>();

        product.getDependantOn().forEach(productArticleDependency -> productArticleDependency.getArticle().getDependantProduct().forEach(productForUpdate -> productsMarkedForStockChange.add(productForUpdate.getInventoryProduct())));
        productsMarkedForStockChange.forEach(productForStockUpdate -> calculateAndUpdatePossibleQuantity(productForStockUpdate.getProductId()));
    }

    /**
     * Updates possible quantity for product based on current article inventory
     * @param productId product to update
     */
    @Transactional
    public void calculateAndUpdatePossibleQuantity(long productId) {
        InventoryProduct productForStockUpdate = inventoryProductRepo.findById(productId).orElseThrow(()->new InventoryException(ErrorCodes.NOT_FOUND,"Cant find product"));
        long possibleQuantity = productForStockUpdate.getDependantOn().stream().mapToLong(productArticleDependency -> productArticleDependency.getArticle().getStock() / productArticleDependency.getRequiredQuantity()).summaryStatistics().getMin();
        inventoryProductRepo.updatePossibleQuantityByProductId(possibleQuantity, productForStockUpdate.getProductId());
    }


    /**
     * Updates products possible quantity and recalculates possible quantity with
     * current inventory articles for all products which may be affected by selling
     * of the given productId
     *
     * @param productId       Id of product which is being sold
     * @param sellingQuantity amount of product being sold
     * @return successful or not
     */

    @Transactional
    public boolean updateProductSales(long productId, long sellingQuantity) {
        InventoryProduct inventoryProduct = inventoryProductRepo.findById(productId).orElseThrow(() -> new InventoryException(ErrorCodes.NOT_FOUND, "Product does not exist"));

        inventoryProduct.getDependantOn().forEach(dependency -> {
            long availableStock = findByArticleId(dependency.getArticle().getArticleId()).getStock();
            long requiredStock = dependency.getRequiredQuantity() * sellingQuantity;
            if (availableStock < requiredStock) {
                throw new InventoryException(ErrorCodes.NOT_ENOUGH_INVENTORY, String.format("Article : %s is low on inventory", dependency.getArticle()));
            }
            InventoryArticle article = dependency.getArticle();
            inventoryItemRepo.updateArticleStockById(article.getArticleId(), requiredStock);
        });
        inventoryProduct = inventoryProductRepo.findById(inventoryProduct.getProductId()).orElseThrow(() -> new InventoryException(ErrorCodes.NOT_FOUND, "Product does not exist"));

        updatePossibleQuantityForRelatedProduct(inventoryProduct);
        return true;
    }

}
