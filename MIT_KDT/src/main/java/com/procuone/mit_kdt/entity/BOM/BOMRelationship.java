package com.procuone.mit_kdt.entity.BOM;
import com.procuone.mit_kdt.entity.BOM.Item;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BOMRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    @ManyToOne
    @JoinColumn(name = "parent_product_code", referencedColumnName = "productCode", nullable = false)
    private Item parentItem; // 상위 부품 (예: 프레임)

    @ManyToOne
    @JoinColumn(name = "child_product_code", referencedColumnName = "productCode", nullable = false)
    private Item childItem; // 하위 부품 (예: 스틸 프레임)

    private Integer quantity; // 하위 부품의 수량
}

