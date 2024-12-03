package com.procuone.mit_kdt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO {

    private Long id; // 재고 고유 ID
    private Long itemId; // Item 테이블의 ID
    private String itemName; // 아이템 이름
    private Integer currentQuantity; // 현재 재고 수량
    private Integer reservedQuantity; // 예약된 수량
    private Integer minimumRequired; // 최소 유지 수량
    private String coooperationCompanyInvertoryId; // 재고 위치 정보
    private LocalDateTime lastUpdated; // 마지막 업데이트 시간
}