package com.myhomeshop.inventory.warehouse.dataloaders;

import com.myhomeshop.inventory.warehouse.dataloaders.dataformat.ProductLoaderDataFormat;
import com.myhomeshop.inventory.warehouse.dataloaders.exceptions.InventoryDataLoaderException;
import com.myhomeshop.inventory.warehouse.entities.InventoryProduct;
import com.myhomeshop.inventory.warehouse.entities.ProductArticleDependency;
import com.myhomeshop.inventory.warehouse.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


/**
 * InventoryProductLoaderRoute
 * This is a Camel route which runs inside application monitoring input directories
 * represented by properties for products.json and loads all the {@link InventoryProduct} from the
 * file into database
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryProductLoaderRoute extends RouteBuilder {

    @Value("${product.file.path}")
    private String productLoadFilePath;
    @Value("${product.file.name}")
    private String productLoadFileName;
    @Value("${product.failure.file.path}")
    private String productFileFailurePath;


    private final InventoryService itemService;


    /**
     * Definition of camel route for loading articles and handling of invalid articles
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        onException(ValidationException.class,InventoryDataLoaderException.class)
                .setHeader("CamelFileName",simple("${body.name}.json"))
                .marshal(new JacksonDataFormat(ProductLoaderDataFormat.ProductRecord.class))
                .to(LoaderUtils.getFileEndPointFromPath(productFileFailurePath,null))
                .handled(true);

        from(LoaderUtils.getFileEndPointFromPath(productLoadFilePath,productLoadFileName))
                    .routeId("productLoaderRoute")
                    .unmarshal(new JacksonDataFormat(ProductLoaderDataFormat.class))
                    .split().simple("${body.products}")
                    .process(this::processProducts);

    }


    /**
     * Extracts {@link InventoryProduct} from the exchange and adds to database and validates
     * all the articles for product has already been inserted into database
     *
     * @param exchange camel exchange payload containing marshalled json for single Product
     */
    private void processProducts(Exchange exchange) {
        ProductLoaderDataFormat.ProductRecord productRecord = exchange.getIn().getBody(ProductLoaderDataFormat.ProductRecord.class);

        boolean allArticlesInInventory = productRecord.getContain_articles().stream().allMatch(articleRecord -> itemService.findByArticleId(articleRecord.getArt_id())!=null);
        if(!allArticlesInInventory){
            throw new InventoryDataLoaderException("Articles on which this product is dependant on may not have yet been inserted");
        }

        List<ProductArticleDependency> productArticleDependency =productRecord.getContain_articles().stream().map(articleRecord ->{

            ProductArticleDependency dependency = new ProductArticleDependency();
            dependency.setArticle(itemService.findByArticleId(articleRecord.getArt_id()));
            dependency.setRequiredQuantity(articleRecord.getAmount_of());
            return dependency;}).collect(Collectors.toList());


        InventoryProduct product =
                InventoryProduct.builder()
                    .productName(productRecord.getName())
                    .dependantOn(productArticleDependency)
                    .build();
        product.getDependantOn().forEach(dependency->{ dependency.setInventoryProduct(product);dependency.getArticle().getDependantProduct().add(dependency);});
        itemService.insertProductAndDependencies(product);
        log.debug("Inserted product with Id :{}, dependencies : {}" ,product.getProductId(),product.getDependantOn());
    }

}
