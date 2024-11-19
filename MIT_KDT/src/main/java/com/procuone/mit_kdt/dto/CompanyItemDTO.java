package com.procuone.mit_kdt.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyItemDTO {
    private Long id;              // CompanyItem ID
    private String businessId;    // Company의 Business ID
    private String itemCode;      // Item의 Product Code
    private Integer leadTime;     // 생산 L/T
    private Integer supplyUnit;   // 최소 공급 단위
    private Integer productionQty;// 최소 생산 수량
    private Integer unitCost;     // 단가
}
