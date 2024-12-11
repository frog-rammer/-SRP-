package com.procuone.mit_kdt.dto.ItemDTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BOMRelationshipDTO {
    private Long parentItemId; // 상위 부품 ID
    private Long childItemId; // 하위 부품 ID
    private Integer quantity; // 하위 부품의 수량
}
