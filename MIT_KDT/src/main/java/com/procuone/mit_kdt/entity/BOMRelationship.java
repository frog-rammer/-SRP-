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
public class BOMRelationship {

    @EmbeddedId
    private BOMRelationshipId id = new BOMRelationshipId();

    @ManyToOne
    @MapsId("parentItemId")
    @JoinColumn(name = "parent_item_id")
    private Item parentItem;

    @ManyToOne
    @MapsId("childItemId")
    @JoinColumn(name = "child_item_id")
    private Item childItem;

    private Integer quantity;
}

@Embeddable
class BOMRelationshipId implements Serializable {

    private Long parentItemId;
    private Long childItemId;
}
