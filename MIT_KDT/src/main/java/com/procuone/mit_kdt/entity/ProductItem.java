package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductItem {

    @EmbeddedId
    private ProductItemId id = new ProductItemId();

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer quantity;
}

@Embeddable
class ProductItemId implements Serializable {

    private Long productId;
    private Long itemId;
}