package com.myhomeshop.inventory.warehouse.dataloaders;

import com.myhomeshop.inventory.warehouse.dataloaders.dataformat.ArticleLoaderDataFormat;
import com.myhomeshop.inventory.warehouse.dataloaders.exceptions.InventoryDataLoaderException;
import com.myhomeshop.inventory.warehouse.entities.InventoryArticle;
import com.myhomeshop.inventory.warehouse.entities.InventoryProduct;
import com.myhomeshop.inventory.warehouse.entities.ProductArticleDependency;
import com.myhomeshop.inventory.warehouse.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import java.util.*;


/**
 * InventoryArticleLoaderRoute
 * This is a Camel route which runs inside application monitoring input directories
 * represented by properties for inventory.json and loads all the Articles from the
 * file into database
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryArticleLoaderRoute extends RouteBuilder {

    @Value("${inventory.file.path}")
    private String inventoryLoadFilePath;
    @Value("${inventory.file.name}")
    private String inventoryLoadFileName;

    @Value("${inventory.failure.file.path}")
    private String articleFileFailurePath;

    private final InventoryService itemService;

    private static final Set<InventoryProduct> affectedProduct = new HashSet<>();

    /**
     * Definition of camel route for loading articles and handling of invalid articles
     *
     * @throws Exception exceptions which are not handled by route
     */
    @Override
    public void configure() throws Exception {

        onException(ValidationException.class, InventoryDataLoaderException.class)
                .setHeader("CamelFileName", simple("${body.name}.json"))
                .marshal(new JacksonDataFormat(ArticleLoaderDataFormat.ArticleRecord.class))
                .to(LoaderUtils.getFileEndPointFromPath(articleFileFailurePath, null))
                .handled(true);

        from(LoaderUtils.getFileEndPointFromPath(inventoryLoadFilePath, inventoryLoadFileName))
                .onCompletion()
                .process(this::updateDependantProducts)
                .end()
                .routeId("articleLoaderRoute")
                .unmarshal(new JacksonDataFormat(ArticleLoaderDataFormat.class))
                .split().simple("${body.inventory}")
                .process(this::processArticles);
    }

    /**
     * Invoked after completion of Article inventory load. updates available quantity of products dependant on updated products
     * @param exchange exchange body for camel route
     */
    private void updateDependantProducts(Exchange exchange) {
        log.debug("Article inventory update job finished");
        log.debug("starting dependant products quantity update");
        affectedProduct.forEach(product ->
                itemService.calculateAndUpdatePossibleQuantity(product.getProductId()));
    }

    /**
     * Extracts {@link InventoryArticle} from the exchange and adds to database
     *
     * @param exchange camel exchange payload containing marshalled json for single Article
     */
    private void processArticles(Exchange exchange) {
        ArticleLoaderDataFormat.ArticleRecord article = exchange.getIn().getBody(ArticleLoaderDataFormat.ArticleRecord.class);
        InventoryArticle inventoryArticle = itemService.findByArticleId(article.getArt_id());

        if(inventoryArticle==null) {

         inventoryArticle=
                 InventoryArticle.builder()
                    .articleId(article.getArt_id())
                    .articleName(article.getName())
                    .stock(article.getStock())
                    .build();
            itemService.insertArticle(inventoryArticle);
            return;
        }
        inventoryArticle.setArticleName(article.getName());
        inventoryArticle.setStock(article.getStock());
        InventoryArticle savedArticle = itemService.insertArticle(inventoryArticle);



        if (savedArticle.getDependantProduct()==null||savedArticle.getDependantProduct().isEmpty()){
            log.debug("inserted article with Id : {} Name : {}", inventoryArticle.getArticleId(), inventoryArticle.getArticleName());
            return;
        }

        savedArticle.getDependantProduct()
                .stream()
                .map(ProductArticleDependency::getInventoryProduct)
                .filter(Objects::nonNull)
                .forEach(affectedProduct::add);

        log.debug("inserted article with Id : {} Name : {}", inventoryArticle.getArticleId(), inventoryArticle.getArticleName());
    }
}
