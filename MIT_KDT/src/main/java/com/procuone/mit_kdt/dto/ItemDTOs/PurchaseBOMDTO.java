package com.procuone.mit_kdt.dto.ItemDTOs;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseBOMDTO {
    private Long id;
    private String productCode;
    private Long itemId;   // Item 엔티티 대신 ID 사용
    private String businessId; // Company 엔티티 대신 businessId 사용 (String 타입)
    private Integer quantity;
    private Integer leadTime;  // Lead Time
    private Integer unitCost;  // 단가



//    @JsonManagedReference
//    private List<PurchaseBOMDTO> children;  // 자식 BOM 리스트 추가
}