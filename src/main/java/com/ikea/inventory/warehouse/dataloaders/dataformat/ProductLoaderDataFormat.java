package com.ikea.inventory.warehouse.dataloaders.dataformat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.camel.Converter;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * ProductLoaderDataFormat
 * POJO class for unmarshalling input JSON from inventory.json
 */
@Data
@NoArgsConstructor
public class ProductLoaderDataFormat {

    private List<ProductRecord> products;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductRecord{
        @Size(min = 3, max = 255,message = "Product name must be between 3 to 255 characters")
        private String name;
        private List<ArticleRecord> contain_articles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleRecord{
        private long art_id;
        @Positive(message = "Product should depend on at least 1 quantity of article")
        private long amount_of;
    }


}
