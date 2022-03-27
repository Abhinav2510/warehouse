package com.ikea.inventory.warehouse.dataloaders.dataformat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * ArticleLoaderDataFormat
 *  POJO class for unmarshalling input JSON from inventory.json
 */
@Data
@NoArgsConstructor
public class ArticleLoaderDataFormat {
    private List<ArticleRecord> inventory;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArticleRecord {
        private long art_id;
        private String name;
        private long stock;
    }
}

