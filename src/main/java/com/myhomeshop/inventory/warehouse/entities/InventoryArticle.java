package com.myhomeshop.inventory.warehouse.entities;


import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "INVENTORY_ARTICLES")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "dependantProduct")
@Validated
public class InventoryArticle {

    @Id
    private long articleId;
    @Size(min = 3,max = 255,message = "articleName must be between 3 to 255 characters")
    private String articleName;
    @PositiveOrZero
    private long stock;

    @OneToMany(mappedBy = "article",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    List<ProductArticleDependency> dependantProduct;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryArticle article = (InventoryArticle) o;
        return articleId == article.articleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId);
    }
}
