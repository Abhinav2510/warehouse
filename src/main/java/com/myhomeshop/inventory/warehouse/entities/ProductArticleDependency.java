package com.myhomeshop.inventory.warehouse.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Positive;

@Entity
@Table(name = "PRODUCT_ARTICLE_DEPENDANCY")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductArticleDependency {
    @Id
    @GeneratedValue
    private long productItemDependencyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "INVENTORY_ARTICLE_ID")
    private InventoryArticle article;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PRODUCT_ID")
    private InventoryProduct inventoryProduct;

    @Positive
    private long requiredQuantity;

}
