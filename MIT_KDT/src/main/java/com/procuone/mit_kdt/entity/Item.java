package com.procuone.mit_kdt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    private String name;

    private String specifications;

    @ManyToOne
    @JoinColumn(name = "product_id")  // 외래키로 연결된 Product
    private Product product;  // Product와의 관계 설정

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "parentItem")
    private Set<BOMRelationship> childItems;

    @OneToMany(mappedBy = "childItem")
    private Set<BOMRelationship> parentItems;
}
