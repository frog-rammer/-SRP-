package com.procuone.mit_kdt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {

    @Id
    private String productId;
    private String productName;
    private String productDimension;
    private String productLargeCategory;
    private String productMediumCategory;
    private String productSmallCategory;
    private String productMaterial;
    private String productDrawingFile;
    private String productQuantity;
}
