package com.procuone.mit_kdt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CompanyInventoryDTO {

    private Long id; // 재고 고유 ID
    private String businessId; // 업체의 Business ID
    private String companyName; // 업체명
    private Long itemId; // 품목 ID
    private String itemName; // 품목명
    private Integer currentQuantity; // 현재 재고 수량
    private Integer reservedQuantity; // 예약된 수량
    private String lastUpdated; // 마지막 업데이트 시간

}
