package com.procuone.mit_kdt.entity.BOM;

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
public class BOMRelationship {

    @EmbeddedId
    private BOMRelationshipId id = new BOMRelationshipId();

    @ManyToOne
    @MapsId("parentItemId")
    @JoinColumn(name = "parent_item_id")
    private Item parentItem; // 상위 부품 (예: 프레임)

    @ManyToOne
    @MapsId("childItemId")
    @JoinColumn(name = "child_item_id")
    private Item childItem; // 하위 부품 (예: 스틸 프레임)

    private Integer quantity; // 하위 부품의 수량
}

@Embeddable
class BOMRelationshipId implements Serializable {

    private Long parentItemId; // 상위 부품 ID
    private Long childItemId;  // 하위 부품 ID
}
