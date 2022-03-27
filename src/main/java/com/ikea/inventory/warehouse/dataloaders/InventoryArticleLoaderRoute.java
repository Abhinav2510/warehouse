package com.ikea.inventory.warehouse.dataloaders;

import com.ikea.inventory.warehouse.dataloaders.dataformat.ArticleLoaderDataFormat;
import com.ikea.inventory.warehouse.dataloaders.exceptions.InventoryDataLoaderException;
import com.ikea.inventory.warehouse.entities.InventoryArticle;
import com.ikea.inventory.warehouse.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.validation.ValidationException;


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

    /**
     * Definition of camel route for loading articles and handling of invalid articles
     * @throws Exception
     */
    @Override
    public void configure() throws Exception {

        onException(ValidationException.class, InventoryDataLoaderException.class)
                .setHeader("CamelFileName", simple("${body.name}.json"))
                .marshal(new JacksonDataFormat(ArticleLoaderDataFormat.ArticleRecord.class))
                .to(LoaderUtils.getFileEndPointFromPath(articleFileFailurePath,null))
                .handled(true);

        from(LoaderUtils.getFileEndPointFromPath(inventoryLoadFilePath,inventoryLoadFileName))
                .routeId("articleLoaderRoute")
                .unmarshal(new JacksonDataFormat(ArticleLoaderDataFormat.class))
                .split().simple("${body.inventory}")
                .process(this::processArticles);
    }

    /**
     * Extracts {@link InventoryArticle} from the exchange and adds to database
     * @param exchange camel exchange payload containing marshalled json for single Article
     */
    private void processArticles(Exchange exchange) {
        ArticleLoaderDataFormat.ArticleRecord article = exchange.getIn().getBody(ArticleLoaderDataFormat.ArticleRecord.class);
        InventoryArticle inventoryArticle =
                InventoryArticle.builder()
                        .articleId(article.getArt_id())
                        .articleName(article.getName())
                        .stock(article.getStock())
                        .build();

        itemService.insertArticle(inventoryArticle);
        log.debug("inserted article with Id : {} Name : {}", inventoryArticle.getArticleId(), inventoryArticle.getArticleName());
    }




}
